package com.insutil.textanalysis.repository;


import com.insutil.textanalysis.model.Code;
import com.insutil.textanalysis.model.dto.SimpleCode;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CodeRepository extends R2dbcRepository<Code, Long> {
    @Query("select * from t_ins_code")
    Flux<SimpleCode> getAll();

    @Query("select * from t_ins_code where enabled = 1")
    Flux<Code> findEnabledCodes();

    @Query("select * from t_ins_code where enabled = 1 and id = :id")
    Mono<Code> findEnabledCodeById(Long id);

    @Query("select * from t_ins_code where enabled = 1 and code_id = :codeId")
    Mono<SimpleCode> findEnabledCodeByCodeId(String codeId);

    @Query("select * from t_ins_code where enabled = 1 and parent_id = :parentId")
    Flux<SimpleCode> findEnabledCodeByParentId(Long parentId);

    @Modifying
    @Query("update t_ins_code set active = :isActivate where id in (" +
            "with recursive code as (" +
            "select id from t_ins_code where id = :id " +
            "union all " +
            "select child.id from t_ins_code as child, code as parent " +
            "where parent.id = child.parent_id) " +
            "select id from code)")
    Mono<Integer> activateCode(Long id, boolean isActivate);
}
