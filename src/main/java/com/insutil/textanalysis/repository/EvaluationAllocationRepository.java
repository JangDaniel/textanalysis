package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.Allocation;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface EvaluationAllocationRepository extends R2dbcRepository<Allocation, Long> {
//	@Query(
//		"select a.*, u.user_id, u.user_name " +
//		"from t_qa_user u " +
//		"left join t_ta_stt_evaluation_allocation a on a.evaluator_id = u.id and a.date = :date " +
//		"where u.role_id = 3 " +
//		"and u.enabled is TRUE "
//	)
	Flux<Allocation> findAllByDate(LocalDate date);
}
