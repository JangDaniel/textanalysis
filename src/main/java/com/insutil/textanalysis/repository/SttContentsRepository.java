package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.STTContents;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SttContentsRepository extends R2dbcRepository<STTContents, Long> {

    @Query("select * from t_ta_stt_contents where call_date = :callDate and state_code = :stateCode")
    Flux<STTContents> findSTTContentsByCallDateAndStateCode(String callDate, long stateCode);

    @Query("select * from t_ta_stt_contents where contract_no = :contractNo")
    Flux<STTContents> findSTTContentsByContractNo(String contractNo);

    @Modifying
    @Query("update t_ta_stt_contents set state_code = :stateCode where id = :id")
    Mono<Integer> updateStateCode(long stateCode, long id);


}
