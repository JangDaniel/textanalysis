package com.insutil.textanalysis.handler;

import com.insutil.textanalysis.model.Allocation;
import com.insutil.textanalysis.repository.CodeRepository;
import com.insutil.textanalysis.repository.EvaluationAllocationRepository;
import com.insutil.textanalysis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class EvaluationAllocationHandler {
	private final EvaluationAllocationRepository evaluationAllocationRepository;
	private final UserRepository userRepository;
	private final CodeRepository codeRepository;

	public Mono<ServerResponse> findByDate(ServerRequest request) {
		LocalDate date = LocalDate.parse(request.pathVariable("date"));
		return evaluationAllocationRepository.findAllByDate(date)
			.switchIfEmpty(
				copyPreviousAllocations(date)
			)
			.flatMap(allocation ->
				Mono.just(allocation).zipWith(userRepository.findById(allocation.getEvaluatorId()))
				.map(tuple -> tuple.getT1().withEvaluator(tuple.getT2()))
			)
			.flatMap(allocation ->
				Mono.just(allocation).zipWith(codeRepository.findById(allocation.getInsuranceType()))
				.map(tuple -> tuple.getT1().withInsuranceTypeCode(tuple.getT2()))
			)
			.collectList()
			.flatMap(ServerResponse.ok()::bodyValue);
	}

	protected Flux<Allocation> copyPreviousAllocations(LocalDate date) {
		return evaluationAllocationRepository.findAllByDate(date.minusMonths(1))
			.map(allocation ->
				// 이전달 allocation 정보를 복사해서 생성한다.
				Allocation.builder()
					.date(date)
					.evaluatorId(allocation.getEvaluatorId())
					.insuranceType(allocation.getInsuranceType())
					.count(allocation.getCount())
					.build()
			)
			.flatMap(evaluationAllocationRepository::save)
			.switchIfEmpty(
				// 이전달 allocation 정보가 없으면 평가사별로 default count 를 갖는 allocation 을 생성한다
				userRepository.findAllByEnabledIsTrueAndEvaluatorIsTrue()
					.flatMap(evaluator ->
						evaluationAllocationRepository.save(
							Allocation.builder()
								.date(date)
								.evaluatorId(evaluator.getId())
								.build()
						)
					)
					.flatMap(allocation ->
						// 다시 조회하는 이유는 DB에 설정된 default value 는 save 함수의 결과에는 담기지 않기 때문이다.
						evaluationAllocationRepository.findById(allocation.getId())
					)
			);
	}

	public Mono<ServerResponse> saveAll(ServerRequest request) {
		return request.bodyToFlux(Allocation.class)
			.flatMap(evaluationAllocationRepository::save)
			.collectList()
			.flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
	}

	public Mono<ServerResponse> updateAll(ServerRequest request) {
		return request.bodyToFlux(Allocation.class)
			.flatMap(evaluationAllocationRepository::save)
			.collectList()
			.flatMap(ServerResponse.ok()::bodyValue)
			.switchIfEmpty(ServerResponse.notFound().build());
	}

	public Mono<ServerResponse> findAllEvaluators(ServerRequest request) {
		return userRepository.findAllByEnabledIsTrueAndEvaluatorIsTrue()
			.collectList()
			.flatMap(ServerResponse.ok()::bodyValue);
	}
}
