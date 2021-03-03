package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.SttSentences;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SttSentencesRepository extends R2dbcRepository<SttSentences, Long> {

}
