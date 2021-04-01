package com.insutil.textanalysis.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ContractObjectRepositoryTest {

    @Autowired
    ContractObjectRepository contractObjectRepository;

    @Test
    public void findEnabledProductByModelCodeTest() {
        contractObjectRepository.findContractObjectBySttContentsId(1177)
                .as(StepVerifier::create)
                .expectNextCount(85)
                .verifyComplete();
    }

}