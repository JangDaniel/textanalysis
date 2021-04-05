package com.insutil.textanalysis.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.LocalDate;

@SpringBootTest
public class SttEvaluationRepositoryTest {
	@Autowired
	private SttEvaluationRepository repository;

	@Test
	void findCountByInsuranceType() {
		repository.findAllAllocatedCount(LocalDate.parse("2021-03-08"), 25L)
			.doOnNext(System.out::println)
			.as(StepVerifier::create)
			.expectNextCount(3)
			.verifyComplete();
	}
}
