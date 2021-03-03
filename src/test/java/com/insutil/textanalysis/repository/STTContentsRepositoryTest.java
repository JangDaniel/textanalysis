package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.STTContents;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Slf4j
@SpringBootTest
class STTContentsRepositoryTest {

    @Autowired
    SttContentsRepository sttContentsRepository;


    @Test
    void findSTTContentsByCallDateAndStateCodeTest() {
        Flux<STTContents> sttContentsFlux = sttContentsRepository.findSTTContentsByCallDateAndStateCode("20190219", 33);
        StepVerifier.create(sttContentsFlux)
                .expectNextCount(1451)
                .verifyComplete();

    }


}