package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.AllocationBreak;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface AllocationBreakRepository extends R2dbcRepository<AllocationBreak, Long> {

	@Query("select * from t_ta_allocation_break where evaluator_id = :evaluatorId " +
		"and ((start_date >= :startDate and start_date <= :endDate) " +
		"or (end_date >= :startDate and end_date <= :endDate))"
	)
	Flux<AllocationBreak> findAllByEvaluatorIdAndStartDateAfterOrEndDateBefore(Long evaluatorId, LocalDate startDate, LocalDate endDate);
//	Flux<AllocationBreak> findAllByDateBetweenAndEvaluatorId(LocalDate from, LocalDate to, Long evaluatorId);
}
