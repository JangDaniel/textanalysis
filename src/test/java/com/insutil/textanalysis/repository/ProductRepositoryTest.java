package com.insutil.textanalysis.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Test
    public void findEnabledProductByModelCodeTest() {
        productRepository.findByModelCode("M006003001")
                .as(StepVerifier::create)
                .assertNext(p -> {
                    assertThat(p.getModelCode()).isEqualTo("M006003001");
                    assertThat(p.getId()).isEqualTo(9);
                })
                .verifyComplete();
    }
}