package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.SttEvaluation;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface SttEvaluationRepository extends R2dbcRepository<SttEvaluation, Long> {
	@Query(
		"select e.* from t_ta_stt_evaluation e " +
		"left join t_ta_stt_contents s on s.id = e.stt_id " +
		"where s.call_date = :callDate"
	)
	Flux<SttEvaluation> findAllByCallDate(String callDate);

	@Query(
		"select e.* from t_ta_stt_evaluation e " +
		" left join t_ta_stt_contents s on s.id = e.stt_id " +
		" where s.call_date = :callDate " +
		" and s.agent_id = :agentId"
	)
	Flux<SttEvaluation> findAllByCallDateAndAgentId(LocalDate callDate, Long agentId);

	Mono<SttEvaluation> findBySttId(Long sttId);
}
