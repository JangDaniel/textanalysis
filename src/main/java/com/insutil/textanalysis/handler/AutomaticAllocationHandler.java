package com.insutil.textanalysis.handler;

import com.insutil.textanalysis.model.*;
import com.insutil.textanalysis.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutomaticAllocationHandler {
	private final SttContentsRepository sttContentsRepository;
	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final CodeRepository codeRepository;
	private final UserRepository userRepository;
	private final EvaluationAllocationRepository evaluationAllocationRepository;
	private final AllocationBreakRepository allocationBreakRepository;
	private final HolidayRepository holidayRepository;
	private final SttEvaluationRepository sttEvaluationRepository;
	/*
	 * 모든 STT 에 대해서 평가한다.
	 * STT 배분은 실시간으로 이루어 진다.
	 * 평가사는 보종별로 설정한 갯수만큼 배분받는다. 보종은 Motor, 장기, 일반 으로 구분한다.
	 * 할당 기준은, allocation 의 count 가 남아있으면서 할당된 갯수가 적은 평가사에게 할당한다.
	 * 자동 배분 후 남는 STT 는 수동으로 배분 할 수 있어야 한다.
	 * 배분한 결과는 t_ta_stt_evaluation 테이블에 입력한다.
	 */


	public Mono<SttEvaluation> evaluate(Long sttId) {
		// 1. stt의 보종 찾기
		// 2. 평가사와 평사사 별 allocation 조회
		// 3. 해당 보종에 대해서 할당량이 남은 평가사 중에서 가장 할당된 갯수가 적은(자동 배분 설정된 count 와 배분된 count 의 차이가 큰) 평가사 찾기
		// 4. 찾은 평가사에게 할당(stt_evaluation 테이블에 기록)
		// 5. 만약 2번 단계에서 평가사를 찾지 못하면 미할당 상태로 저장

		return getSttContents(sttId)
			.flatMap(sttContents ->
				findEvaluatorToAllocate(sttContents.getCallDate(), sttContents.getProduct().getProductCategory().getInsuranceType())
				.flatMap(allocationCount ->
					sttEvaluationRepository.save(
						SttEvaluation.builder()
							.sttId(sttId)
							.insuranceType(allocationCount.getInsuranceType())
							.evaluatorId(allocationCount.getEvaluatorId())
						.build()
					)
				)
				.onErrorResume(throwable ->
					sttEvaluationRepository.save(
						SttEvaluation.builder()
							.sttId(sttId)
							.insuranceType(sttContents.getProduct().getProductCategory().getInsuranceType())
							.build()
					)
				)
			);
	}

	public Mono<AllocationCount> findEvaluatorToAllocate(LocalDate date, Long insuranceType) {
		return getEvaluators(date)
			.map(evaluator -> {
				Allocation foundAllocation = evaluator.getAllocations().stream()
					.filter(allocation -> Objects.equals(allocation.getInsuranceType(), insuranceType))
					.findFirst()
					.orElse(Allocation.builder().count(0).build());
				return AllocationCount.builder()
					.evaluatorId(evaluator.getId())
					.insuranceType(foundAllocation.getInsuranceType())
					.count(foundAllocation.getCount())
					.build();
			})
			.flatMap(allocationCount ->
				Mono.just(allocationCount).zipWith(
					getAllocatedCountByEvaluator(date, allocationCount.getEvaluatorId(), insuranceType)
				)
			)
			.map(tuple -> {
				tuple.getT1().setCount(tuple.getT1().getCount() - tuple.getT2().getCount());
				return tuple.getT1();
			})
			.filter(allocationCount -> allocationCount.getCount() > 0)
			.collectList().log("filtered allocationCounts")
			.map(allocationCounts ->
				allocationCounts.stream().max((o1, o2) -> o1.getCount() - o2.getCount())
			)
			.map(Optional::get).log();
	}

	/**
	 * STT 의 product 에 맞는 보종 반환
	 * @param sttId
	 * @return Mono<Code> code 는 보모 codeId 가 INSURANCE_TYPE 인 code 이다.
	 */
	protected Mono<STTContents> getSttContents(Long sttId) {
		return sttContentsRepository.findById(sttId)
			.flatMap(sttContents ->
				Mono.just(sttContents).zipWith(
					productRepository.findByModelCode(sttContents.getSimilarityCode())
					.flatMap(product ->
						Mono.just(product).zipWith(
							categoryRepository.findById(product.getCategory())
							.flatMap(category ->
								Mono.just(category).zipWith(
									codeRepository.findById(category.getInsuranceType())
								)
								.map(tuple -> tuple.getT1().withIncuranceTypeCode(tuple.getT2()))
							)// Mono<Category>
						)
						.map(tuple -> tuple.getT1().withProductCategory(tuple.getT2()))
					)// Mono<Product>
				)
				.map(tuple -> tuple.getT1().withProduct(tuple.getT2()))
			) // Mono<STTContents>
		;
	}

	/**
	 * 평가사 조회
	 * 평가사 별로 설정된 자동배분 설정 정보도 같이 반환
	 * @return Flux<User>
	 */
	protected Flux<User> getEvaluators(LocalDate date) {
		// date 는 stt 상담 날짜 이지만 allocation 은 date 가 포함된 월의 allocation 을 구하기 때문에 date 의 day 는 1일 이어야 한다.
		return userRepository.findAllByEnabledIsTrueAndEvaluatorIsTrue()
			.flatMap(user->
				Mono.just(user).zipWith(getAllocations(user.getId(), date.withDayOfMonth(1)).collectList())
				.map(tuple -> tuple.getT1().withAllocations(tuple.getT2()))
			);
	}

	/**
	 * 평가사 별 allocation 정보(보종별 자동 배분 설정)
	 * @param evaluatorId
	 * @return Flux<Allocation> 설정된 보종 수 만큼의 allocation 반환
	 */
	protected Flux<Allocation> getAllocations(Long evaluatorId, LocalDate date) {
//		LocalDate now = LocalDate.now();
//		LocalDate date = LocalDate.of(now.getYear(), now.getMonth(), 1);
		return evaluationAllocationRepository.findByDateAndEvaluatorId(date, evaluatorId) // 이번달 allocation 조회
			.switchIfEmpty(
				evaluationAllocationRepository.findByDateAndEvaluatorId(date.minusMonths(1), evaluatorId) // 지난달  allocation 도 없으면 이번달 allocation 새로 생성
				.switchIfEmpty(
					evaluationAllocationRepository.save(
						Allocation.builder()
							.date(date)
							.evaluatorId(evaluatorId)
						.build()
					)
					.flatMap(allocation ->
						evaluationAllocationRepository.findById(allocation.getId())
					)
				)
			);
	}

	/**
	 * 평가사에게 할당된 stt evaluation 을 insuranceType 별로 조회
	 * allocation 에 설정된 count 보다 작을경우 할당 대상이 된다
	 * @param date
	 * @param evaluatorId
	 */
	protected Flux<GroupedFlux<Long, SttEvaluation>> getCurrentEvaluations(LocalDate date, Long evaluatorId) {
		return sttEvaluationRepository.findAllByCallDateAndAgentId(date, evaluatorId)
			.groupBy(SttEvaluation::getInsuranceType)
			;
	}

	/**
	 * 평가사에게 할당된 stt 갯수를 date 와 보종에 따라 조회
	 * @param date
	 * @param insuranceType
	 * @return
	 */
	protected Flux<AllocatedCount> getAllocatedCount(LocalDate date, Long insuranceType) {
		return sttEvaluationRepository.findAllAllocatedCount(date, insuranceType);
	}

	/**
	 * 평가사에게 할당된 STT count 조회
	 */
	protected Mono<AllocatedCount> getAllocatedCountByEvaluator(LocalDate date, Long evaluatorId, Long insuranceType) {
		return sttEvaluationRepository.findAllocatedCountByEvaluatorId(date, evaluatorId, insuranceType);
	}
}
