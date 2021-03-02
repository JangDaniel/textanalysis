package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.ScriptCriterion;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ScriptCriteriaRepository extends R2dbcRepository<ScriptCriterion, Long> {
	@Query("select c.* , count(d.id) as childCount" +
		" from t_ta_script_criteria c" +
		" left join t_ta_script_detail d on d.criterion_id = c.id" +
		" where c.enabled = 1" +
		" and c.script_id = :scriptId" +
		" group by c.id" +
		" order by c.parent_id, c.sort asc")
	Flux<ScriptCriterion> findAllByScriptId(Long scriptId);

//	@Query("select c.* , count(d.id) as childCount" +
//		" from t_ta_script_criteria c" +
//		" left join t_ta_script_detail d on d.criterion_id = c.id" +
//		" where c.enabled = 1" +
//		" and d.enabled = 1" +
//		" and c.id = :id")
//	Mono<ScriptCriterion> getEnabledById(Long id);

	Flux<ScriptCriterion> findAllByEnabledIsTrue();
	Flux<ScriptCriterion> findAllByEnabledIsTrueAndProductId(Long productId);
	Mono<ScriptCriterion> findByEnabledTrueAndId(Long id);
}
