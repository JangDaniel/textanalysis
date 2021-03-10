package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.CallEvaluation;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface CallEvaluationRepository extends R2dbcRepository<CallEvaluation, Long> {
	@Query("select e.* from t_ta_call_evaluation e" +
		" left join t_ta_stt_contents s on s.id = e.stt_id" +
		" where s.call_date = :callDate")
	Flux<CallEvaluation> findAllByCallDate(String callDate);
}
