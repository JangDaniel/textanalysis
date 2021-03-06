package com.insutil.textanalysis.handler;

import com.insutil.textanalysis.model.*;
import com.insutil.textanalysis.model.dto.SimpleCode;
import com.insutil.textanalysis.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
public class EvaluationHandler {
	private final SttEvaluationRepository sttEvaluationRepository;
	private final CriterionEvaluationRepository criterionEvaluationRepository;
	private final ScriptMatchRepository scriptMatchRepository;
	private final SttContentsRepository sttContentsRepository;
	private final ScriptCriteriaRepository scriptCriteriaRepository;
	private final ScriptDetailRepository scriptDetailRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final SimpleCode evaluationDoneState;

	@Autowired
	public EvaluationHandler(
		SttEvaluationRepository sttEvaluationRepository,
		CriterionEvaluationRepository criterionEvaluationRepository,
		ScriptMatchRepository scriptMatchRepository,
		SttContentsRepository sttContentsRepository,
		ScriptCriteriaRepository scriptCriteriaRepository,
		ScriptDetailRepository scriptDetailRepository,
		ProductRepository productRepository,
		UserRepository userRepository,
		CodeRepository codeRepository
	) {
		this.sttEvaluationRepository = sttEvaluationRepository;
		this.criterionEvaluationRepository = criterionEvaluationRepository;
		this.scriptMatchRepository = scriptMatchRepository;
		this.sttContentsRepository = sttContentsRepository;
		this.scriptCriteriaRepository = scriptCriteriaRepository;
		this.scriptDetailRepository = scriptDetailRepository;
		this.productRepository = productRepository;
		this.userRepository = userRepository;
		this.evaluationDoneState = codeRepository.findByCodeId("ESC_DONE").block();
	}


	public Mono<ServerResponse> findAll(ServerRequest request) {
		return sttEvaluationRepository.findAllByEnabledIsTrueOrderByIdDesc()
			.flatMap(this::getSttEvaluationWiths)
			.collectList()
			.flatMap(ServerResponse.ok()::bodyValue);
	}
	public Mono<ServerResponse> findAllByQuery(ServerRequest request) {
		String now = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
		String fromDate = request.queryParam("fromDate").orElse(now);
		String toDate = request.queryParam("toDate").orElse(now);
		String offset = request.queryParam("offset").orElse("none");
		String limit = request.queryParam("limit").orElse("none");
		String evaluator = request.queryParam("evaluator").orElse("all");

		if (offset.equals("none")) {
			return sttEvaluationRepository.findAllByQuery(LocalDate.parse(fromDate), LocalDate.parse(toDate))
				.flatMap(this::getSttEvaluationWiths)
				.collectList()
				.flatMap(ServerResponse.ok()::bodyValue);
		}
		else if (evaluator.equals("all")) {
			return sttEvaluationRepository.findAllByQuery(LocalDate.parse(fromDate), LocalDate.parse(toDate), Integer.parseInt(offset), Integer.parseInt(limit))
				.flatMap(this::getSttEvaluationWiths)
				.collectList()
				.flatMap(ServerResponse.ok()::bodyValue);
		}
		return sttEvaluationRepository.findAllByQuery(LocalDate.parse(fromDate), LocalDate.parse(toDate), Integer.parseInt(offset), Integer.parseInt(limit), Long.parseLong(evaluator))
			.flatMap(this::getSttEvaluationWiths)
			.collectList()
			.flatMap(ServerResponse.ok()::bodyValue);
	}
	public Mono<ServerResponse> findAllByNotAllocated(ServerRequest request) {
		String now = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
		String fromDate = request.queryParam("fromDate").orElse(now);
		String toDate = request.queryParam("toDate").orElse(now);
		String offset = request.queryParam("offset").orElse("none");
		String limit = request.queryParam("limit").orElse("none");

		if (offset.equals("none")) {
			return sttEvaluationRepository.findAllByNotAllocated(LocalDate.parse(fromDate), LocalDate.parse(toDate))
				.flatMap(this::getSttEvaluationWiths)
				.collectList()
				.flatMap(ServerResponse.ok()::bodyValue);
		}
		return sttEvaluationRepository.findAllByNotAllocated(LocalDate.parse(fromDate), LocalDate.parse(toDate), Integer.parseInt(offset), Integer.parseInt(limit))
			.flatMap(this::getSttEvaluationWiths)
			.collectList()
			.flatMap(ServerResponse.ok()::bodyValue);
	}

	public Mono<ServerResponse> getEvaluationRate(ServerRequest request) {

		String fromDateTime = request.pathVariable("fromDateTime");
		String toDateTime = request.pathVariable("toDateTime");
		
		List<LocalDateTime> dateTimes = makeWhereDateParam(fromDateTime, toDateTime);
		return sttEvaluationRepository.getEvaluationRateData(dateTimes.get(0), dateTimes.get(1))
				.doOnNext(data -> data.setProcessingRate(Math.round(Float.valueOf(data.getProcessingRate()) * 100) + "%"))
				.collectList()
				.flatMap(ServerResponse.ok()::bodyValue);
	}

	private List<LocalDateTime> makeWhereDateParam(String fromDateTime, String toDateTime) {
		if(StringUtils.hasText(fromDateTime) && StringUtils.hasText(toDateTime)) {
			LocalDateTime paramFromTime = LocalDateTime.parse(fromDateTime, DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm"));
			LocalDateTime paramToTime = LocalDateTime.parse(toDateTime, DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm"));

			return Arrays.asList(paramFromTime, paramToTime);
		}

		return Arrays.asList(LocalDateTime.now(), LocalDateTime.now());
	}

	public Mono<ServerResponse> getTotalCountByQuery(ServerRequest request) {
		String now = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
		String fromDate = request.queryParam("fromDate").orElse(now);
		String toDate = request.queryParam("toDate").orElse(now);
		String evaluator = request.queryParam("evaluator").orElse("all");
		if (evaluator.equals("all")) {
			return sttEvaluationRepository.getTotalCount(LocalDate.parse(fromDate), LocalDate.parse(toDate)).flatMap(ServerResponse.ok()::bodyValue);
		}
		return sttEvaluationRepository.getTotalCount(LocalDate.parse(fromDate), LocalDate.parse(toDate), Long.parseLong(evaluator)).flatMap(ServerResponse.ok()::bodyValue);

	}

	public Mono<ServerResponse> getTotalCountByNotAllocated(ServerRequest request) {
		String now = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
		String fromDate = request.queryParam("fromDate").orElse(now);
		String toDate = request.queryParam("toDate").orElse(now);
		return sttEvaluationRepository.getTotalCountByNotAllocated(LocalDate.parse(fromDate), LocalDate.parse(toDate)).flatMap(ServerResponse.ok()::bodyValue);
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

	protected Mono<STTContents> getSttContentsBySttId(Long sttId) {
		return sttContentsRepository.findById(sttId)
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
			});
	}

	protected Mono<CriterionEvaluationSummary> getCriterionEvaluationSummary(SttEvaluation sttEvaluation ,ScriptCriterion scriptCriterion) {
		return Flux.fromIterable(scriptCriterion.getScriptDetails())
			.flatMap(scriptDetail ->
				// scriptDetail ??? similarity score ??? ?????? ?????? script ??? stt_script_match ???????????? ????????????
				findScriptMatch(sttEvaluation.getSttId(), scriptDetail.getId())
			)
//			.switchIfEmpty(
//				// TODO: stt_script_match ??????????????? product ??? ?????? criterion ??? script detail ??? 1?????? ????????? ???????????? record ???  ????????? ??????.
//				// ????????? ????????? CriterionEvaluationSummary ??? ?????????
//				// ???????????? ??? ????????? ?????????????????? ?????????
//				Flux.just(CriterionEvaluationDetail.builder()
//					.script("????????? ?????????????????? ????????? ???????????????.")
//					.build())
//			)
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
			;
	}

	/*
	STT evaluation id ??? TA ????????? STT ??? ?????? ??? ????????? ???????????? ?????? ?????????
	 */
	public Mono<ServerResponse> findSttEvaluationSummaries(ServerRequest request) {
		String sttEvaluationId = request.pathVariable("sttEvaluationId");
		if (!NumberUtils.isDigits(sttEvaluationId)) {
			return ServerResponse.badRequest().bodyValue(sttEvaluationId);
		}
		return sttEvaluationRepository.findById(Long.valueOf(sttEvaluationId))
			.flatMap(sttEvaluation ->
				Mono.just(sttEvaluation).zipWith(getSttContentsBySttId(sttEvaluation.getSttId()))
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
							getCriterionEvaluationSummary(sttEvaluation, scriptCriterion)

						)
						.collectList()
						.flatMap(summaries ->
							// tree ????????? criterion ?????? ????????? root CriterionEvaluationSummary ??? ?????? (??? 120?????? CriterionEvaluationSummary ??? 24?????? ??????)
							Flux.fromIterable(summaries)
								.filter(summary -> summary.getParentCriterionId() == null)
								.flatMap(mainSummary ->
									findCriterionEvaluationDetailsRecursively(mainSummary, Flux.fromIterable(summaries))
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

	/**
	 * TODO : 35?????? stt ?????? ????????? 1.6M ?????? ????????? 4?????? ?????????.
	 * TODO : matchRate ??? score ??? ???????????? ????????? ?????? criteria ??? scriptDetail ??? ?????? client ??? ???????????? ????????? ??????
	 * TODO : matchRate ??? score ????????? ?????? client ??? ?????????.
	 * TODO : scriptMatches ??? ????????? ????????? ????????? ????????????.
	 * const scriptMatches = item.scriptMatches;
	 * let matchRate = 0;
	 * if (scriptMatches.length > 0) {
	 *     const rateSum = scriptMatches
	 *         .map((scriptMatch) => scriptMatch.matchRate)
	 *         .reduce((acc, cur) => {
	 *             if (cur === null) return acc + 0;
	 *             return acc + cur;
	 *         });
	 *     matchRate = rateSum / scriptMatches.length;
	 * }
	 * let score = 0;
	 * if (item.criterionEvaluations.length > 0) {
	 *     score = item.criterionEvaluations
	 *         .map((criterionEvaluation) => criterionEvaluation.score)
	 *         .reduce((acc, cur) => {
	 *             if (cur === null) return acc + 0;
	 *             return acc + cur;
	 *         });
	 * }
	 */
	protected Mono<SttEvaluation> getSttEvaluationWiths(SttEvaluation _sttEvaluation) {
		return Mono.just(_sttEvaluation)
			.flatMap(sttEvaluation ->
				Mono.just(sttEvaluation).zipWith(userRepository.findById(sttEvaluation.getEvaluatorId()))
				.map(tuple -> tuple.getT1().withEvaluator(tuple.getT2()))
			)
			.onErrorResume(throwable -> Mono.just(_sttEvaluation))
//			.flatMap(sttEvaluation ->
//				Mono.just(sttEvaluation).zipWith(getCriterionEvaluations(sttEvaluation.getId()).collectList())
//					.map(tuple -> tuple.getT1().withCriterionEvaluations(tuple.getT2()))
//			)
			.flatMap(sttEvaluation ->
				Mono.just(sttEvaluation).zipWith(
					getCriterionEvaluations(sttEvaluation.getId())
						.reduce(0, (acc, criterionEvaluation) -> acc + (criterionEvaluation.getScore() == null ? 0 : criterionEvaluation.getScore()))
				)
				.map(tuple -> tuple.getT1().withScore(tuple.getT2()))
			)
//			.flatMap(sttEvaluation ->
//				Mono.just(sttEvaluation).zipWith(scriptMatchRepository.findBySttId(sttEvaluation.getSttId()).collectList())
//					.map(tuple -> tuple.getT1().withScriptMatches(tuple.getT2()))
//			)
			.flatMap(sttEvaluation ->
				Mono.just(sttEvaluation).zipWith(
					scriptMatchRepository.findBySttId(sttEvaluation.getSttId())
					.map(scriptMatch ->
						AccModel.builder().value(scriptMatch.getSimilarityScore()).count(1).build()
					)
					.reduce(AccModel.builder().value(0).count(0).build() , (acc, cur) -> {
						acc.addValue(cur.getValue());
						acc.addCount();
						return acc;
					})
					.map(accModel -> {
						if (accModel.getValue() > 0.0f && accModel.getCount() > 0) {
							accModel.setValue(accModel.getValue() / accModel.getCount());
						}
						return accModel;
					})
				)
				.map(tuple -> tuple.getT1().withMatchRate(tuple.getT2().getValue()))
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
			.map(tuple -> tuple.getT1().withStt(tuple.getT2()))
			;
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
//			.zipWith(getScriptCriteria(productId).collectList())
//			.map(tuple -> tuple.getT1().withScriptCriteria(tuple.getT2()))
			;
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
					.map(sttEvaluation -> {
						if (origin.getEvaluationDate() == null && Objects.equals(sttEvaluation.getStateId(), evaluationDoneState.getId())) {
							// ?????? ?????? ??? ????????? ??????
							sttEvaluation.setEvaluationDate(LocalDate.now());
						}
						if (origin.getEvaluatorId() == null && sttEvaluation.getEvaluatorId() != null) {
							// ????????? ?????? ??????
							sttEvaluation.setAllocationDate(LocalDate.now());
						}
						return sttEvaluation;
					})
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

	public Mono<ServerResponse> updateSimpleSttEvaluation(ServerRequest request) {
		String id = request.pathVariable("id");
		if (!NumberUtils.isDigits(id)) {
			return ServerResponse.badRequest().bodyValue(id);
		}
		return sttEvaluationRepository.findById(Long.valueOf(id))
			.flatMap(origin ->
				request.bodyToMono(SttEvaluation.class)
					.map(sttEvaluation -> {
						if (origin.getEvaluatorId() == null && sttEvaluation.getEvaluatorId() != null) {
							// ????????? ?????? ??????
							sttEvaluation.setAllocationDate(LocalDate.now());
						}
						return sttEvaluation;
					})
					.flatMap(sttEvaluation ->
						sttEvaluationRepository.save(origin.update(sttEvaluation))
					)
					.flatMap(this::getSttEvaluationWiths)
			)
			.flatMap(ServerResponse.ok()::bodyValue)
			.switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).build());
	}
}
