package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.dto.SimpleCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CodeRepositoryTest {

    @Autowired
    CodeRepository codeRepository;

    @Test
    public void findEnabledCodeByParentIdTest() {
        codeRepository.findEnabledCodeByParentId(31L).collectList().block().forEach(System.out::println);
    }

    @Test
    public void findEnabledCodeByCodeIdTest() {
        Mono<SimpleCode> simpleCodeMono = codeRepository.findEnabledCodeByCodeId("BM");
        StepVerifier.create(simpleCodeMono)
                .consumeNextWith(t -> System.out.println(t))
                .verifyComplete();

    }
}