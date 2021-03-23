package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.SttEvaluation;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SttEvaluationRepository extends R2dbcRepository<SttEvaluation, Long> {
	@Query("select e.* from t_ta_stt_evaluation e" +
		" left join t_ta_stt_contents s on s.id = e.stt_id" +
		" where s.call_date = :callDate")
	Flux<SttEvaluation> findAllByCallDate(String callDate);

	Mono<SttEvaluation> findBySttId(Long sttId);
}
