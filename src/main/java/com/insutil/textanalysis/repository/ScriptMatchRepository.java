package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.CriterionEvaluationDetail;
import com.insutil.textanalysis.model.ScriptMatch;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ScriptMatchRepository extends R2dbcRepository<ScriptMatch, Long> {
	@Query("select sm.script_detail_id, sd.script, sm.mapped_script, ss.unit_sentence, max(sm.similarity_score) as similarity_score, sd.score as base_score, sc.name as criterion_name, sc.sort as criterion_sort " +
		"from t_ta_stt_script_match sm " +
		"left join t_ta_stt_sentences ss on ss.id = sm.stt_sentence_id " +
		"left join t_ta_script_detail sd on sd.id = sm.script_detail_id " +
		"left join t_ta_script_criteria sc on sc.id = sd.criterion_id " +
		"where sm.script_detail_id = :scriptDetailId " +
		"and ss.stt_id = :sttId " +
		"order by sc.sort"
	)
	Mono<CriterionEvaluationDetail> findBySttIdAndScriptDetailId(Long sttId, Long scriptDetailId);

	@Query("select m.* " +
		"from t_ta_stt_script_match m " +
		"left join t_ta_stt_sentences s on s.id = m.stt_sentence_id " +
		"where s.stt_id = :sttId")
	Flux<ScriptMatch> findBySttId(Long sttId);
}
