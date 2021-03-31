package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {
	/**
	 * 평가사 조회
	 * @return Flux<User>
	 */
	Flux<User> findAllByEnabledIsTrueAndEvaluatorIsTrue();
}
