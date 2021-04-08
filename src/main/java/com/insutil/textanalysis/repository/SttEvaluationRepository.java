package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.AllocatedCount;
import com.insutil.textanalysis.model.SttEvaluation;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface SttEvaluationRepository extends R2dbcRepository<SttEvaluation, Long> {
	@Query("select count(*) " +
		"from t_ta_stt_evaluation e " +
		"left join t_ta_stt_contents s on s.id = e.stt_id " +
		"where e.enabled is true " +
		"and s.call_date between :fromDate and :toDate " +
		"order by e.id desc ")
	Mono<Integer> getTotalCount(LocalDate fromDate, LocalDate toDate);

	@Query("select count(*) " +
		"from t_ta_stt_evaluation e " +
		"left join t_ta_stt_contents s on s.id = e.stt_id " +
		"where e.enabled is true " +
		"and s.call_date between :fromDate and :toDate " +
		"and e.evaluator_id = :evaluatorId " +
		"order by e.id desc ")
	Mono<Integer> getTotalCount(LocalDate fromDate, LocalDate toDate, Long evaluatorId);

	@Query("select * from t_ta_stt_evaluation where enabled is true order by id desc limit :offset, :limit")
	Flux<SttEvaluation> findAllByLimit(int offset, int limit);

	@Query("select e.* " +
		"from t_ta_stt_evaluation e " +
		"left join t_ta_stt_contents s on s.id = e.stt_id " +
		"where e.enabled is true " +
		"and s.call_date between :fromDate and :toDate " +
		"order by e.id desc")
	Flux<SttEvaluation> findAllByQuery(LocalDate fromDate, LocalDate toDate);

	@Query("select e.* " +
		"from t_ta_stt_evaluation e " +
		"left join t_ta_stt_contents s on s.id = e.stt_id " +
		"where e.enabled is true " +
		"and s.call_date between :fromDate and :toDate " +
		"order by e.id desc " +
		"limit :offset, :limit")
	Flux<SttEvaluation> findAllByQuery(LocalDate fromDate, LocalDate toDate, int offset, int limit);

	@Query("select e.* " +
		"from t_ta_stt_evaluation e " +
		"left join t_ta_stt_contents s on s.id = e.stt_id " +
		"where e.enabled is true " +
		"and s.call_date between :fromDate and :toDate " +
		"and e.evaluator_id = :evaluatorId " +
		"order by id desc " +
		"limit :offset, :limit")
	Flux<SttEvaluation> findAllByQuery(LocalDate fromDate, LocalDate toDate, int offset, int limit, Long evaluatorId);

	Flux<SttEvaluation> findAllByEnabledIsTrueOrderByIdDesc();

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

	/**
	 * 평가사에게 date 에 할당 된 보종이 insuranceType 인 stt 갯수
	 * @param date
	 * @param insuranceType
	 * @return
	 */
	@Query(
		"select se.evaluator_id, se.insurance_type, count(*) as count " +
		"from t_ta_stt_evaluation se " +
		"left join t_ta_stt_contents sc on sc.id = se.stt_id " +
		"where sc.call_date = :date " +
		"and se.insurance_type = :insuranceType " +
		"and se.evaluator_id is not null" +
		"group by evaluator_id"
	)
	Flux<AllocatedCount> findAllAllocatedCount(LocalDate date, Long insuranceType);

	@Query(
		"select se.evaluator_id, se.insurance_type, count(*) as count " +
		"from t_ta_stt_evaluation se " +
		"left join t_ta_stt_contents sc on sc.id = se.stt_id " +
		"where sc.call_date = :date " +
		"and se.evaluator_id = :evaluatorId " +
		"and se.insurance_type = :insuranceType"
	)
	Mono<AllocatedCount> findAllocatedCountByEvaluatorId(LocalDate date, Long evaluatorId, Long insuranceType);
}
