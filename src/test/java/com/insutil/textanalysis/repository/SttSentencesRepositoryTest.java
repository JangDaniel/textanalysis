package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.SttSentences;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SttSentencesRepositoryTest {

    @Autowired
    SttSentencesRepository sttSentencesRepository;

    @Test
    public void insertTest() {
        SttSentences sttSentences = SttSentences.builder()
                .sttId(1L)
                .unitSentence("고객님 아까 말씀 드린 것처럼 제가 주소지 는 변경 해 드렸구요 고객님")
                .morpheme("[고객,말씀,제가,주소지,변경,고객]")
                .scriptDetailId(null)
                .similarityScore(null).build();
        sttSentencesRepository.save(sttSentences);

    }
}