package com.insutil.textanalysis.handler;

import com.insutil.textanalysis.analysis.SentenceManager;
import com.insutil.textanalysis.common.analysis.PosTagging;
import com.insutil.textanalysis.model.ScriptCriterion;
import com.insutil.textanalysis.model.ScriptDetail;
import com.insutil.textanalysis.model.ScriptDetailMainWord;
import com.insutil.textanalysis.repository.ScriptCriteriaRepository;
import com.insutil.textanalysis.repository.ScriptDetailMainWordRepository;
import com.insutil.textanalysis.repository.ScriptDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScriptHandler {
	private final ScriptCriteriaRepository scriptCriteriaRepository;
	private final ScriptDetailRepository scriptDetailRepository;
	private final ScriptDetailMainWordRepository scriptDetailMainWordRepository;
	private final PosTagging posTagging;

	public Mono<ServerResponse> getScriptCriteriaByProductId(ServerRequest request) {
		String id = request.pathVariable("id");
		if (!NumberUtils.isDigits(id)) {
			return ServerResponse.badRequest().bodyValue(id);
		}
		return scriptCriteriaRepository.findAllByEnabledIsTrueAndProductId(Long.valueOf(id))
			.flatMap(criteria ->
				Mono.just(criteria)
					.zipWith(
						scriptDetailRepository.findAllByCriterionId(criteria.getId())
							.flatMap(scriptDetail ->
								Mono.just(scriptDetail)
									.zipWith(scriptDetailMainWordRepository.findAllByScriptDetailIdAndEnabledIsTrue(scriptDetail.getId()).collectList())
							)
							.map(tuple -> tuple.getT1().withMainWords(tuple.getT2()))
							.collectList()
					)
			)
			.map(tuple -> tuple.getT1().withChildCount(tuple.getT2().size()))
			.collectList()
//			.map(tuple -> tuple.getT1().withScriptDetails(tuple.getT2()))
			.flatMap(ServerResponse.ok()::bodyValue)
			.switchIfEmpty(ServerResponse.notFound().build());
	}

	public Mono<ServerResponse> getScriptCriteria(ServerRequest request) {
		return scriptCriteriaRepository.findAllByEnabledIsTrue()
			.flatMap(criteria ->
				Mono.just(criteria)
					.zipWith(
						scriptDetailRepository.findAllByCriterionId(criteria.getId())
							.flatMap(scriptDetail ->
								Mono.just(scriptDetail)
									.zipWith(scriptDetailMainWordRepository.findAllByScriptDetailIdAndEnabledIsTrue(scriptDetail.getId()).collectList())
							)
							.map(tuple -> tuple.getT1().withMainWords(tuple.getT2()))
							.collectList()
					)
			)
			.map(tuple -> tuple.getT1().withChildCount(tuple.getT2().size()))
			.collectList()
			.flatMap(ServerResponse.ok()::bodyValue);
	}

	public Mono<ServerResponse> saveScriptCriterion(ServerRequest request) {
		return request.bodyToMono(ScriptCriterion.class)
			.flatMap(scriptCriteriaRepository::save)
			.flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue)
			.switchIfEmpty(ServerResponse.badRequest().build());
	}

	public Mono<ServerResponse> updateScriptCriterion(ServerRequest request) {
		String id = request.pathVariable("id");
		if (!NumberUtils.isDigits(id)) {
			return ServerResponse.badRequest().bodyValue(id);
		}
		return scriptCriteriaRepository.findById(Long.valueOf(id))
			.flatMap(origin ->
				request.bodyToMono(ScriptCriterion.class)
					.map(origin::update)
					.flatMap(scriptCriteriaRepository::save)
			)
			.flatMap(scriptCriteriaRepository::save)
			.flatMap(ServerResponse.ok()::bodyValue);
	}

	public Mono<ServerResponse> getScriptCriterionById(ServerRequest request) {
		String id = request.pathVariable("id");
		if (!NumberUtils.isDigits(id)) {
			return ServerResponse.badRequest().bodyValue(id);
		}
		return scriptCriteriaRepository.findByEnabledTrueAndId(Long.valueOf(id))
			.flatMap(criteria ->
				Mono.just(criteria)
					.zipWith(
						scriptDetailRepository.findAllByCriterionId(criteria.getId())
							.flatMap(scriptDetail ->
								Mono.just(scriptDetail)
									.zipWith(scriptDetailMainWordRepository.findAllByScriptDetailIdAndEnabledIsTrue(scriptDetail.getId()).collectList())
							)
							.map(tuple -> tuple.getT1().withMainWords(tuple.getT2()))
							.collectList()
					)
			)
			.map(tuple -> tuple.getT1().withScriptDetails(tuple.getT2()))
			.map(criterion -> {
					criterion.setChildCount(criterion.getScriptDetails().size());
					return criterion;
				}
			)
			.flatMap(ServerResponse.ok()::bodyValue)
			.switchIfEmpty(ServerResponse.notFound().build());
	}

	public Mono<ServerResponse> saveScriptDetail(ServerRequest request) {
		return request.bodyToMono(ScriptDetail.class)
			.flatMap(scriptDetail ->
				scriptDetailRepository.getMaxSortNum(scriptDetail.getCriterionId())
					.map(value -> {
						scriptDetail.setSort(value + 1);
						return scriptDetail;
					})
			)
			.map(scriptDetail -> {
				String script = scriptDetail.getScript();
				if (!script.contains("$")) {
					// script 에 object 가 없을 경우 미리 형태소분석을 해 놓는다
					scriptDetail.setMorpheme(String.join(", ", posTagging.extractNounTag(scriptDetail.getScript())));
				}
				return scriptDetail;
			})
			.flatMap(scriptDetail ->
				scriptDetailRepository.save(scriptDetail)
					.flatMap(savedScriptDetail -> {
						scriptDetail.getMainWords().forEach(scriptDetailMainWord -> scriptDetailMainWord.setScriptDetailId(savedScriptDetail.getId()));
						return Mono.just(savedScriptDetail)
							.zipWith(scriptDetailMainWordRepository.saveAll(scriptDetail.getMainWords()).collectList())
							.map(tuple -> tuple.getT1().withMainWords(tuple.getT2()));
					})
			)
			.flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
	}

	public Mono<ServerResponse> updateScriptDetail(ServerRequest request) {
		String id = request.pathVariable("id");
		if (!NumberUtils.isDigits(id)) {
			return ServerResponse.badRequest().bodyValue(id);
		}
		return scriptDetailRepository.findById(Long.valueOf(id))
			.flatMap(origin ->
				request.bodyToMono(ScriptDetail.class)
					.map(param -> {
						// 형태소 분석된 script 의 경우 script 가 달라졌을 때 형태소 분석을 해 놓는다
						if (origin.getScript().equals(param.getScript())) return param;
						if (param.getScript().contains("$")) {
							// script 가 달라졌는데 object 가 추가됐다면 기존 형태소 분석결과를 삭제한다.
							param.setMorpheme("");
						} else {
							// 새로운 script 에 object 가 없는 경우에 형태소 분석을 한다
							param.setMorpheme(String.join(", ", posTagging.extractNounTag(param.getScript())));
						}
						return param;
					})
					.map(origin::update)
					.flatMap(updatedScriptDetail ->
						scriptDetailRepository.save(updatedScriptDetail)
							.zipWith(Flux.fromIterable(updatedScriptDetail.getMainWords())
								.flatMap(mainWord -> {
									if (mainWord.getId() != null) {
										return updateScriptDetailMainWord(mainWord);
									} else {
										return saveScriptDetailMainWord(mainWord);
									}
								})
								.collectList()
							)
							.map(tuple -> tuple.getT1().withMainWords(tuple.getT2()))
					)
			)
			.flatMap(ServerResponse.ok()::bodyValue)
			.switchIfEmpty(ServerResponse.notFound().build());
	}

	public Mono<ScriptDetailMainWord> updateScriptDetailMainWord(ScriptDetailMainWord word) {
		return scriptDetailMainWordRepository.findById(word.getId())
			.map(origin -> origin.update(word))
			.flatMap(scriptDetailMainWordRepository::save)
			.switchIfEmpty(Mono.empty());
	}

	public Mono<ScriptDetailMainWord> saveScriptDetailMainWord(ScriptDetailMainWord word) {
		return scriptDetailMainWordRepository.save(word);
	}

	public Mono<ServerResponse> disableScriptDetail(ServerRequest request) {
		String id = request.pathVariable("id");
		String updateUser = request.pathVariable("updateUser");
		if (!NumberUtils.isDigits(id) || !NumberUtils.isDigits(updateUser)) {
			return ServerResponse.badRequest().bodyValue(id);
		}
		return scriptDetailRepository.findById(Long.valueOf(id))
			.map(origin -> {
				origin.setEnabled(false);
				origin.setUpdateUser(Long.valueOf(updateUser));
				return origin;
			})
			.flatMap(scriptDetailRepository::save)
			.flatMap(ServerResponse.ok()::bodyValue)
			.switchIfEmpty(ServerResponse.notFound().build());
	}
}
