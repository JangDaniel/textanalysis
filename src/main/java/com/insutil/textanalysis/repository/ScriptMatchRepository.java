package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.EvaluationRow;
import com.insutil.textanalysis.model.ScriptMatch;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface ScriptMatchRepository extends R2dbcRepository<ScriptMatch, Long> {
	@Query("select * from t_ta_stt_script_match where criterion_evaluation_id = :criterionEvaluationId")
	Flux<ScriptMatch> findAllByCriterionEvaluationId(Long criterionEvaluationId);
}
