package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.AllocationBreak;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface AllocationBreakRepository extends R2dbcRepository<AllocationBreak, Long> {

	@Query("select * from t_ta_allocation_break where evaluator_id = :evaluatorId " +
		"and ((start_date >= :startDate and start_date <= :endDate) " +
		"or (end_date >= :startDate and end_date <= :endDate))"
	)
	Flux<AllocationBreak> findAllByEvaluatorIdAndStartDateAfterOrEndDateBefore(Long evaluatorId, LocalDate startDate, LocalDate endDate);

	@Query("select if(count(id) >= 1, true, false) as isBreak\n" +
		"from t_ta_allocation_break " +
		"where evaluator_id = :evaluatorId " +
		"and (start_date <= :date and end_date >= :date)")
	Mono<Boolean> isBreakDay(Long evaluatorId, LocalDate date);

}
