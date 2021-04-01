package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.SttSentences;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SttSentencesRepositoryTest {

    @Autowired
    SttSentencesRepository sttSentencesRepository;

    @Test
    public void insertTest() {
        SttSentences sttSentences = SttSentences.builder()
                .sttId(1L)
                .callDate(LocalDate.now())
                .unitSentence("고객님 아까 말씀 드린 것처럼 제가 주소지 는 변경 해 드렸구요 고객님")
                .morpheme("[고객,말씀,제가,주소지,변경,고객]")
                .build();
        sttSentencesRepository.save(sttSentences)
            .as(StepVerifier::create)
            .assertNext(d -> "[고객,말씀,제가,주소지,변경,고객]".equals(d.getMorpheme()))
                .verifyComplete();

    }

    @Test
    public void deleteSttSentencesByCallDateTest() {
        sttSentencesRepository.deleteSttSentencesByCallDate("20210308")
                .as(StepVerifier::create)
                .expectNext(1)
                .expectComplete()
                .verify();
    }
}