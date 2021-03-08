package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.SttSentences;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SttSentencesRepository extends R2dbcRepository<SttSentences, Long> {
    @Modifying
    @Query("delete from t_ta_stt_sentences where call_date = :callDate")
    Mono<Integer> deleteSttSentencesByCallDate(String callDate);
}
