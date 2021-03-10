package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.CriterionEvaluation;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface CriterionEvaluationRepository extends R2dbcRepository<CriterionEvaluation, Long> {
	Flux<CriterionEvaluation> findAllByCallEvaluationId(Long callEvaluationId);
}
