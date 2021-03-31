package com.insutil.textanalysis.handler;

import com.insutil.textanalysis.model.Allocation;
import com.insutil.textanalysis.repository.EvaluationAllocationRepository;
import com.insutil.textanalysis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class EvaluationAllocationHandler {
	private final EvaluationAllocationRepository evaluationAllocationRepository;
	private final UserRepository userRepository;

	public Mono<ServerResponse> findByDate(ServerRequest request) {
		LocalDate date = LocalDate.parse(request.pathVariable("date"));
		return evaluationAllocationRepository.findAllByDate(date)
			.flatMap(allocation ->
				Mono.just(allocation).zipWith(userRepository.findById(allocation.getEvaluatorId()))
				.map(tuple -> tuple.getT1().withEvaluator(tuple.getT2()))
			)
			.collectList()
			.flatMap(ServerResponse.ok()::bodyValue);
	}

	public Mono<ServerResponse> save(ServerRequest request) {
		return request.bodyToMono(Allocation.class)
			.flatMap(evaluationAllocationRepository::save)
			.flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
	}

	public Mono<ServerResponse> update(ServerRequest request) {
		String id = request.pathVariable("id");
		if (!NumberUtils.isDigits(id)) {
			return ServerResponse.badRequest().bodyValue(id);
		}
		return evaluationAllocationRepository.findById(Long.valueOf(id))
			.flatMap(origin ->
				request.bodyToMono(Allocation.class)
					.map(origin::update)
			)
			.flatMap(evaluationAllocationRepository::save)
			.flatMap(ServerResponse.ok()::bodyValue)
			.switchIfEmpty(ServerResponse.notFound().build());
	}

	public Mono<ServerResponse> findAllEvaluators(ServerRequest request) {
		return userRepository.findAllByEnabledIsTrueAndEvaluatorIsTrue()
			.collectList()
			.flatMap(ServerResponse.ok()::bodyValue);
	}
}
