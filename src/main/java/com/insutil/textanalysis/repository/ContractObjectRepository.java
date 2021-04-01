package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.ContractObject;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ContractObjectRepository extends R2dbcRepository<ContractObject, Long> {
    @Query("select * from t_ta_contract_object where stt_contents_id = :sttContentsId")
    Flux<ContractObject> findContractObjectBySttContentsId(long sttContentsId);
}