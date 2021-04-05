package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {
	/**
	 * 평가사 조회
	 * @return Flux<User>
	 */
	Flux<User> findAllByEnabledIsTrueAndEvaluatorIsTrue();

	@Query(
		"select * " +
		"from t_qa_user " +
		"where enabled is TRUE " +
		"and evaluator is TRUE " +
		"and id not in (" +
		"select u.id " +
		"from t_qa_user u " +
		"left join t_ta_allocation_break ab on ab.evaluator_id = u.id " +
		"where ab.enabled is true " +
		"and :date between start_date and end_date" +
		")"
	)
	Flux<User> findAllByNoBreakDayEvaluators(LocalDate date);
}
