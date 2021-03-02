package com.insutil.textanalysis.handler;

import com.insutil.textanalysis.model.ScriptCriterion;
import com.insutil.textanalysis.model.ScriptDetail;
import com.insutil.textanalysis.repository.ScriptCriteriaRepository;
import com.insutil.textanalysis.repository.ScriptDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScriptHandler {
	private final ScriptCriteriaRepository scriptCriteriaRepository;
	private final ScriptDetailRepository scriptDetailRepository;

	public Mono<ServerResponse> getScriptCriteriaByProductId(ServerRequest request) {
		String id = request.pathVariable("id");
		if (!NumberUtils.isDigits(id)) {
			return ServerResponse.badRequest().bodyValue(id);
		}
		return scriptCriteriaRepository.findAllByEnabledIsTrueAndProductId(Long.valueOf(id))
			.flatMap(criteria ->
				Mono.just(criteria)
					.zipWith(
						scriptDetailRepository.findAllByCriterionId(criteria.getId()).collectList()
					)
			)
//			.map(tuple -> tuple.getT1().withScriptDetails(tuple.getT2()))
			.map(tuple -> tuple.getT1().withChildCount(tuple.getT2().size()))
			.collectList()
			.flatMap(ServerResponse.ok()::bodyValue)
			.switchIfEmpty(ServerResponse.notFound().build());
	}

	public Mono<ServerResponse> getScriptCriteria(ServerRequest request) {
		return scriptCriteriaRepository.findAllByEnabledIsTrue()
			.flatMap(criteria ->
				Mono.just(criteria)
					.zipWith(
						scriptDetailRepository.findAllByCriterionId(criteria.getId()).collectList()
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
					.zipWith(scriptDetailRepository.findAllByCriterionId(criteria.getId()).collectList())
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
			.flatMap(scriptDetailRepository::save)
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
					.map(origin::update)
					.flatMap(scriptDetailRepository::save)
			)
			.flatMap(ServerResponse.ok()::bodyValue)
			.switchIfEmpty(ServerResponse.notFound().build());
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
