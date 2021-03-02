package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.STTContents;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface STTContentsRepository extends R2dbcRepository<STTContents, Long> {

    @Query("select * from t_ta_stt_contents where call_date = :callDate and state_code = :stateCode")
    Flux<STTContents> findSTTContentsByCallDateAndStateCode(String callDate, int stateCode);

}
