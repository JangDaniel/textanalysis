package com.insutil.textanalysis.handler;

import com.insutil.textanalysis.model.*;
import com.insutil.textanalysis.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EvaluationHandler {
	private final SttEvaluationRepository sttEvaluationRepository;
	private final CriterionEvaluationRepository criterionEvaluationRepository;
	private final ScriptMatchRepository scriptMatchRepository;
	private final SttContentsRepository sttContentsRepository;
	private final ScriptCriteriaRepository scriptCriteriaRepository;
	private final ScriptDetailRepository scriptDetailRepository;
	private final SttSentencesRepository sttSentencesRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	public Mono<ServerResponse> findAll(ServerRequest request) {
		return sttEvaluationRepository.findAll()
			.flatMap(this::getSttEvaluationWiths)
			.collectList()
			.flatMap(ServerResponse.ok()::bodyValue);
	}

	public Mono<ServerResponse> findSttEvaluationById(ServerRequest request) {
		String id = request.pathVariable("id");
		if (!NumberUtils.isDigits(id)) {
			return ServerResponse.badRequest().bodyValue(id);
		}
		return sttEvaluationRepository.findById(Long.valueOf(id))
			.flatMap(this::getSttEvaluationWiths)
			.flatMap(ServerResponse.ok()::bodyValue)
			.switchIfEmpty(ServerResponse.notFound().build());
	}

	public Mono<ServerResponse> findSttEvaluationSummaries(ServerRequest request) {
		String sttEvaluationId = request.pathVariable("sttEvaluationId");
		if (!NumberUtils.isDigits(sttEvaluationId)) {
			return ServerResponse.badRequest().bodyValue(sttEvaluationId);
		}
		return sttEvaluationRepository.findById(Long.valueOf(sttEvaluationId))
			.flatMap(sttEvaluation ->
				Mono.just(sttEvaluation).zipWith(
					sttContentsRepository.findById(sttEvaluation.getSttId())
					.flatMap(sttContents ->
						Mono.just(sttContents).zipWith(productRepository.findById(sttContents.getProductId()))
						.map(tuple -> tuple.getT1().withProduct(tuple.getT2()))
					)
					.flatMap(sttContents ->
						Mono.just(sttContents).zipWith(userRepository.findById(sttContents.getAgentId()))
						.map(tuple -> tuple.getT1().withAgent(tuple.getT2()))
					)
					.map(sttContents -> {
						sttContents.setSttText("");
						return sttContents;
					})
				)
				.map(tuple-> tuple.getT1().withStt(tuple.getT2()))
			)
			.flatMap(sttEvaluation ->
				Mono.just(sttEvaluation).zipWith(
				productRepository.findByModelCode(sttEvaluation.getStt().getSimilarityCode())
					.flatMapMany(product ->
						scriptCriteriaRepository.findAllByEnabledIsTrueAndProductId(product.getId())
						.flatMap(scriptCriterion ->
							Mono.just(scriptCriterion).zipWith(scriptDetailRepository.findAllByCriterionId(scriptCriterion.getId()).collectList())
								.map(tuple -> tuple.getT1().withScriptDetails(tuple.getT2()))
						)
					)
					.flatMap(scriptCriterion ->
						Flux.fromIterable(scriptCriterion.getScriptDetails())
							.flatMap(scriptDetail ->
								// scriptDetail 중 similarity score 가 가장 높은 script 만 stt_script_match 테이블에 기록된다
								findScriptMatch(sttEvaluation.getSttId(), scriptDetail.getId())
							)
//							.switchIfEmpty(
//								// TODO: stt_script_match 테이블에는 product 의 모든 criterion 의 script detail 중 1개는 객체와 맵핑되어 record 가  있어야 한다.
//								// 자세한 내용은 CriterionEvaluationSummary 에 기술됨
//								// 그러므로 이 코드가 실행되어서는 안된다
//								Flux.just(CriterionEvaluationDetail.builder()
//									.script("이것은 출력되어서는 안되는 코드입니다.")
//									.build())
//							)
							.collectList()
							.map(criterionEvaluationDetails -> {
								if (criterionEvaluationDetails.size() == 0 && scriptCriterion.getScriptDetails().size() > 0) {
									ScriptDetail scriptDetail = scriptCriterion.getScriptDetails().get(0);
									return Collections.singletonList(
										CriterionEvaluationDetail.builder()
											.scriptDetailId(scriptDetail.getId())
											.script(scriptDetail.getScript())
//											.unitSentence("matched sentences were not found")
											.criterionName(scriptCriterion.getName())
											.criterionSort(scriptCriterion.getSort())
											.baseScore(scriptDetail.getScore())
											.build()
									);
								} else {
									return criterionEvaluationDetails;
								}
							})
							.map(criterionEvaluationDetails ->
								CriterionEvaluationSummary.builder()
									.sttId(sttEvaluation.getSttId())
									.sttEvaluationId(sttEvaluation.getId())
									.parentCriterionId(scriptCriterion.getParentId())
									.criterionId(scriptCriterion.getId())
									.criterionName(scriptCriterion.getName())
									.criterionSort(scriptCriterion.getSort())
									.criterionEvaluationDetails(new ArrayList<>(criterionEvaluationDetails))
									.build()
							)
							.flatMap(criterionEvaluationSummary ->
								criterionEvaluationRepository.findBySttEvaluationIdAndCriterionId(
									criterionEvaluationSummary.getSttEvaluationId(),
									criterionEvaluationSummary.getCriterionId()
								)
								.map(criterionEvaluation -> {
									criterionEvaluationSummary.setCriterionEvaluationId(criterionEvaluation.getId());
									criterionEvaluationSummary.setScore(criterionEvaluation.getScore());
									criterionEvaluationSummary.setOpinion(criterionEvaluation.getOpinion());
									return criterionEvaluationSummary;
								})
								.switchIfEmpty(Mono.just(criterionEvaluationSummary)).log()
							)
					)
					.collectList()
					.flatMap(list ->
						// tree 구조의 criterion 들을 각각의 root CriterionEvaluationSummary 로 통합
						Flux.fromIterable(list)
							.filter(summary -> summary.getParentCriterionId() == null)
							.flatMap(mainSummary ->
								this.findCriterionEvaluationDetailsRecursively(mainSummary, Flux.fromIterable(list))
							)
							.collectList()
					)
				)
				.map(tuple->tuple.getT1().withCriterionEvaluationSummaries(tuple.getT2()))
			)
			.flatMap(ServerResponse.ok()::bodyValue);
	}

	protected Mono<CriterionEvaluationDetail> findScriptMatch(Long sttId, Long scriptDetailId) {
		return scriptMatchRepository.findBySttIdAndScriptDetailId(sttId, scriptDetailId)
			.switchIfEmpty(Mono.empty());
	}

	protected Mono<CriterionEvaluationSummary> findCriterionEvaluationDetailsRecursively(CriterionEvaluationSummary main, Flux<CriterionEvaluationSummary> list) {
		return list.filter(summary -> main.getCriterionId().equals(summary.getParentCriterionId()))
			.flatMap(child ->
				this.findCriterionEvaluationDetailsRecursively(child, list)
			)
			.flatMap(child -> Flux.fromIterable(child.getCriterionEvaluationDetails()))
			.collectList()
			.map(detailList -> {
				main.getCriterionEvaluationDetails().addAll(detailList);
				return main;
			})
			.switchIfEmpty(Mono.just(main))
		;
	}

	public Mono<ServerResponse> findCallEvaluationsByDate(ServerRequest request) {
		return sttEvaluationRepository.findAllByCallDate(request.pathVariable("callDate"))
			.flatMap(this::getSttEvaluationWiths)
			.collectList()
			.flatMap(ServerResponse.ok()::bodyValue);
	}

	protected Mono<SttEvaluation> getSttEvaluationWiths(SttEvaluation _sttEvaluation) {
		return Mono.just(_sttEvaluation)
			.flatMap(sttEvaluation ->
				Mono.just(sttEvaluation).zipWith(userRepository.findById(sttEvaluation.getEvaluatorId()))
					.map(tuple -> tuple.getT1().withEvaluator(tuple.getT2()))
			)
			.flatMap(sttEvaluation ->
				Mono.just(sttEvaluation).zipWith(getCriterionEvaluations(sttEvaluation.getId()).collectList())
					.map(tuple -> tuple.getT1().withCriterionEvaluations(tuple.getT2()))
			)
			.flatMap(sttEvaluation ->
				Mono.just(sttEvaluation).zipWith(scriptMatchRepository.findBySttId(sttEvaluation.getSttId()).collectList())
					.map(tuple -> tuple.getT1().withScriptMatches(tuple.getT2()))
			)
			.zipWith(sttContentsRepository.findById(_sttEvaluation.getSttId())
				.flatMap(sttContents ->
					Mono.just(sttContents).zipWith(getProduct(sttContents.getProductId()))
						.map(tuple -> tuple.getT1().withProduct(tuple.getT2()))
				)
				.flatMap(sttContents ->
					Mono.just(sttContents).zipWith(userRepository.findById(sttContents.getAgentId()))
						.map(tuple -> tuple.getT1().withAgent(tuple.getT2()))
				)
				.map(sttContents -> {
					sttContents.setSttText(null);
					return sttContents;
				})
			)
			.map(tuple -> tuple.getT1().withStt(tuple.getT2()));
	}

	protected Flux<CriterionEvaluation> getCriterionEvaluations(Long sttEvaluationId) {
		return criterionEvaluationRepository.findAllBySttEvaluationId(sttEvaluationId)
			.flatMap(criterionEvaluation ->
				Mono.just(criterionEvaluation)
					.zipWith(scriptCriteriaRepository.findById(criterionEvaluation.getCriterionId()))
					.map(tuple -> tuple.getT1().withCriterion(tuple.getT2()))
			);
	}



	protected Mono<Product> getProduct(Long productId) {
		return productRepository.findById(productId)
			.zipWith(getScriptCriteria(productId).collectList())
			.map(tuple -> tuple.getT1().withScriptCriteria(tuple.getT2()));
	}

	protected Flux<ScriptCriterion> getScriptCriteria(Long productId) {
		return scriptCriteriaRepository.findAllByEnabledIsTrueAndProductId(productId)
			.flatMap(criterion ->
				Mono.just(criterion).zipWith(scriptDetailRepository.findAllByCriterionId(criterion.getId()).collectList())
				.map(tuple -> tuple.getT1().withScriptDetails(tuple.getT2()))
			);
	}

	public Mono<ServerResponse> updateSttEvaluation(ServerRequest request) {
		String id = request.pathVariable("id");
		if (!NumberUtils.isDigits(id)) {
			return ServerResponse.badRequest().bodyValue(id);
		}
		return sttEvaluationRepository.findById(Long.valueOf(id))
			.flatMap(origin ->
				request.bodyToMono(SttEvaluation.class)
					.flatMap(sttEvaluation ->
						sttEvaluationRepository.save(origin.update(sttEvaluation))
						.zipWith(updateCriterionEvaluations(sttEvaluation.getCriterionEvaluations()).collectList())
						.map(tuple -> tuple.getT1().withCriterionEvaluations(tuple.getT2()))
					)
			)
			.flatMap(ServerResponse.ok()::bodyValue)
			.switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).build());
	}

	protected Flux<CriterionEvaluation> updateCriterionEvaluations(List<CriterionEvaluation> criterionEvaluations) {
		return Flux.fromIterable(criterionEvaluations)
			.flatMap(criterionEvaluation -> {
					if (criterionEvaluation.getId() != null) {
						return criterionEvaluationRepository.findById(criterionEvaluation.getId())
							.map(origin -> origin.update(criterionEvaluation))
							.flatMap(criterionEvaluationRepository::save);
					} else {
						return criterionEvaluationRepository.save(criterionEvaluation);
					}
				}
			);
	}
}
