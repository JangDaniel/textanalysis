package com.insutil.textanalysis.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.LocalDate;

@SpringBootTest
public class EvaluationAllocationRepositoryTest {
	@Autowired
	private EvaluationAllocationRepository repository;
	@Test
	void testFindAllByDate() {
		LocalDate date = LocalDate.of(2021, 3, 1);
		repository.findAllByDate(date)
			.doOnNext(System.out::println)
			.as(StepVerifier::create)
			.expectNextCount(3)
			.verifyComplete();
	}
}
