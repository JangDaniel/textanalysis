package com.insutil.textanalysis.handler;

import com.insutil.textanalysis.model.Allocation;
import com.insutil.textanalysis.model.AllocationBreak;
import com.insutil.textanalysis.repository.AllocationBreakRepository;
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
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

@Component
@RequiredArgsConstructor
public class AllocationBreakHandler {
	private final AllocationBreakRepository repository;

	public Mono<ServerResponse> findAllByDateAndEvaluatorId(ServerRequest request) {
		String _date = request.pathVariable("date");
		String evaluatorId = request.pathVariable("evaluatorId");
		LocalDate date = LocalDate.parse(_date);
		LocalDate firstDate = date.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate lastDate = date.with(TemporalAdjusters.lastDayOfMonth());
		return repository.findAllByEvaluatorIdAndStartDateAfterOrEndDateBefore(Long.valueOf(evaluatorId), firstDate, lastDate).log()
			.collectList()
			.flatMap(ServerResponse.ok()::bodyValue);
	}

	public Mono<ServerResponse> save(ServerRequest request) {
		return request.bodyToMono(AllocationBreak.class)
			.flatMap(repository::save)
			.flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
	}

	public Mono<ServerResponse> update(ServerRequest request) {
		String id = request.pathVariable("id");
		return repository.findById(Long.valueOf(id))
			.flatMap(origin ->
				request.bodyToMono(AllocationBreak.class)
				.map(origin::update)
			)
			.flatMap(repository::save)
			.flatMap(ServerResponse.ok()::bodyValue)
			.switchIfEmpty(ServerResponse.notFound().build());
	}
}
