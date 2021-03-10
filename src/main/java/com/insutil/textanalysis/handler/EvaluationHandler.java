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

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EvaluationHandler {
	private final CallEvaluationRepository callEvaluationRepository;
	private final CriterionEvaluationRepository criterionEvaluationRepository;
	private final ScriptMatchRepository scriptMatchRepository;
	private final SttContentsRepository sttContentsRepository;
	private final ScriptCriteriaRepository scriptCriteriaRepository;
	private final ScriptDetailRepository scriptDetailRepository;
	private final SttSentencesRepository sttSentencesRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	public Mono<ServerResponse> findAll(ServerRequest request) {
		return callEvaluationRepository.findAll()
			.flatMap(this::getCallEvaluationWiths)
			.collectList()
			.flatMap(ServerResponse.ok()::bodyValue);
	}

	public Mono<ServerResponse> findCallEvaluationById(ServerRequest request) {
		String id = request.pathVariable("id");
		if (!NumberUtils.isDigits(id)) {
			return ServerResponse.badRequest().bodyValue(id);
		}
		return callEvaluationRepository.findById(Long.valueOf(id))
			.flatMap(this::getCallEvaluationWiths)
			.flatMap(ServerResponse.ok()::bodyValue)
			.switchIfEmpty(ServerResponse.notFound().build());
	}

	public Mono<ServerResponse> findCallEvaluationsByDate(ServerRequest request) {
		return callEvaluationRepository.findAllByCallDate(request.pathVariable("callDate"))
			.flatMap(this::getCallEvaluationWiths)
			.collectList()
			.flatMap(ServerResponse.ok()::bodyValue);
	}

	protected Mono<CallEvaluation> getCallEvaluationWiths(CallEvaluation _callEvaluation) {
		return Mono.just(_callEvaluation)
			.flatMap(callEvaluation ->
				Mono.just(callEvaluation).zipWith(userRepository.findById(callEvaluation.getEvaluatorId()))
					.map(tuple -> tuple.getT1().withEvaluator(tuple.getT2()))
			)
			.flatMap(callEvaluation ->
				Mono.just(callEvaluation).zipWith(getCriterionEvaluations(callEvaluation.getId()).collectList())
					.map(tuple -> tuple.getT1().withCriterionEvaluations(tuple.getT2()))
			)
			.zipWith(sttContentsRepository.findById(_callEvaluation.getSttId())
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

	protected Flux<CriterionEvaluation> getCriterionEvaluations(Long callEvaluationId) {
		return criterionEvaluationRepository.findAllByCallEvaluationId(callEvaluationId)
			.flatMap(criterionEvaluation ->
				Mono.just(criterionEvaluation)
					.zipWith(scriptCriteriaRepository.findById(criterionEvaluation.getCriterionId()))
					.map(tuple -> tuple.getT1().withCriterion(tuple.getT2()))
			)
			.flatMap(criterionEvaluation ->
				Mono.just(criterionEvaluation)
					.zipWith(getScriptMatches(criterionEvaluation.getId()).collectList())
					.map(tuple -> tuple.getT1().withScriptMatches(tuple.getT2()))
			);
	}

	protected Flux<ScriptMatch> getScriptMatches(Long criterionEvaluationId) {
		return scriptMatchRepository.findAllByCriterionEvaluationId(criterionEvaluationId).log()
			.flatMap(scriptMatch ->
				Mono.just(scriptMatch).zipWith(scriptDetailRepository.findById(scriptMatch.getScriptDetailId()))
					.map(tuple -> tuple.getT1().withScriptDetail(tuple.getT2()))
			)
			.flatMap(scriptMatch ->
				Mono.just(scriptMatch).zipWith(sttSentencesRepository.findById(scriptMatch.getSttSentenceId()))
					.map(tuple -> tuple.getT1().withSttSentence(tuple.getT2()))
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

	// CallEvaluation 정보는 front-end 에서 생성하지 않는다
//	public Mono<ServerResponse> saveCallEvaluation(ServerRequest request) {
//		return request.bodyToMono(CallEvaluation.class)
//			.flatMap(callEvaluation ->
//				callEvaluationRepository.save(callEvaluation)
//				.flatMap(saved ->
//					Mono.just(saved)
//						.zipWith(createCriterionEvaluations(saved.getId(), callEvaluation.getCriterionEvaluations()).collectList())
//						.map(tuple->tuple.getT1().withCriterionEvaluations(tuple.getT2()))
//				)
//			)
//			.flatMap(ServerResponse.ok()::bodyValue);
//	}

	/*
	saveCallEvaluation 함수에 종속
	 */
	protected Flux<CriterionEvaluation> createCriterionEvaluations(Long callEvaluationId, List<CriterionEvaluation> criterionEvaluations) {
		return Flux.fromIterable(criterionEvaluations)
			.flatMap(criterionEvaluation -> {
				criterionEvaluation.setCallEvaluationId(callEvaluationId);
				return criterionEvaluationRepository.save(criterionEvaluation)
					.flatMap(savedCriterionEvaluation ->
						Mono.just(savedCriterionEvaluation).zipWith(createScriptMatches(savedCriterionEvaluation.getId(), criterionEvaluation.getScriptMatches()).collectList())
							.map(tuple -> tuple.getT1().withScriptMatches(tuple.getT2()))
					);
			});
	}

	/*
	createCriterionEvaluations 함수에 종속
	 */
	protected Flux<ScriptMatch> createScriptMatches(Long criterionEvaluationId, List<ScriptMatch> scriptMatches) {
		scriptMatches.forEach(scriptMatch -> scriptMatch.setCriterionEvaluationId(criterionEvaluationId));
		return scriptMatchRepository.saveAll(scriptMatches);
	}

	public Mono<ServerResponse> updateCallEvaluation(ServerRequest request) {
		String id = request.pathVariable("id");
		if (!NumberUtils.isDigits(id)) {
			return ServerResponse.badRequest().bodyValue(id);
		}
		return callEvaluationRepository.findById(Long.valueOf(id))
			.flatMap(origin ->
				request.bodyToMono(CallEvaluation.class)
					.flatMap(callEvaluation ->
						callEvaluationRepository.save(origin.update(callEvaluation))
						.zipWith(updateCriterionEvaluations(callEvaluation.getCriterionEvaluations()).collectList())
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
//							.zipWith(updateScriptMatches(criterionEvaluation.getScriptMatches()).collectList())
//							.map(tuple -> tuple.getT1().withScriptMatches(tuple.getT2())); // ScriptMatch 정보는 front-end 에서 변경하지 않는다
					} else {
						return criterionEvaluationRepository.save(criterionEvaluation);
					}
				}
			);
	}

	protected Flux<ScriptMatch> updateScriptMatches(List<ScriptMatch> scriptMatches) {
		return Flux.fromIterable(scriptMatches)
			.flatMap(scriptMatch ->
				scriptMatchRepository.findById(scriptMatch.getId())
					.map(origin -> origin.update(scriptMatch))
					.flatMap(scriptMatchRepository::save)
			);
	}

}
