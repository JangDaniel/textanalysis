package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.ScriptDetailMainWord;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ScriptDetailMainWordRepository extends R2dbcRepository<ScriptDetailMainWord, Long> {
	Flux<ScriptDetailMainWord> findAllByScriptDetailId(Long scriptDetailId);
}
