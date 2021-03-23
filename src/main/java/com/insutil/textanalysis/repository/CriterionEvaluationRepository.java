package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.CriterionEvaluation;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CriterionEvaluationRepository extends R2dbcRepository<CriterionEvaluation, Long> {
	Flux<CriterionEvaluation> findAllBySttEvaluationId(Long callEvaluationId);
	Mono<CriterionEvaluation> findBySttEvaluationIdAndCriterionId(Long callEvaluationId, Long criterionId);
}
