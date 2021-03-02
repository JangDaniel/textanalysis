package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.ScriptDetail;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ScriptDetailRepository extends R2dbcRepository<ScriptDetail, Long> {
	@Query("select * from t_ta_script_detail where enabled = 1 and criterion_id = :criterionId order by sort")
	Flux<ScriptDetail> findAllByCriterionId(Long criterionId);

	@Query("select ifnull(max(sort), 0) from t_ta_script_detail where criterion_id = :criterionId")
	Mono<Integer> getMaxSortNum(Long criterionId);
}
