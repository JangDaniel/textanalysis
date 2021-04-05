package com.insutil.textanalysis.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

@SpringBootTest
public class AutomaticAllocationHandlerTest {
	@Autowired AutomaticAllocationHandler handler;

	@Test
	void evaluate() {
		// 1341, 319, 863
		Mono.just(863L)
			.flatMap(handler::evaluate)
			.doOnNext(System.out::println)
			.as(StepVerifier::create)
			.expectNextCount(1)
			.verifyComplete();
	}

	@Test
	void findEvaluatorToAllocate() {
		handler.findEvaluatorToAllocate(LocalDate.parse("2021-03-08"), 25L)
			.doOnNext(System.out::println)
			.as(StepVerifier::create)
			.expectNextCount(1)
			.verifyComplete();
	}

	@Test
	void getAllocatedCount() {
		// 할당 된 stt 의 count 조회
		handler.getAllocatedCount(LocalDate.parse("2021-03-08"), 25L)
			.doOnNext(System.out::println)
			.as(StepVerifier::create)
			.expectNextCount(3)
			.verifyComplete();
	}

	@Test
	void getAllocatedCountByEvaluator() {
		handler.getAllocatedCountByEvaluator(LocalDate.parse("2021-03-08"), 13L, 25L)
			.doOnNext(System.out::println)
			.as(StepVerifier::create)
			.expectNextCount(1)
			.verifyComplete();
	}

	@Test
	void getSttContents() {
		handler.getSttContents(720L)
			.doOnNext(System.out::println)
			.as(StepVerifier::create)
			.expectNextCount(1)
			.verifyComplete();
	}

	@Test
	void getEvaluators() {
		handler.getEvaluators(LocalDate.parse("2021-03-08")).doOnNext(System.out::println)
			.as(StepVerifier::create)
			.expectNextCount(4)
			.verifyComplete();
	}

	@Test
	void getAllocations() {
		handler.getAllocations(13L, LocalDate.parse("2021-03-01")).doOnNext(System.out::println)
			.as(StepVerifier::create)
			.expectNextCount(3)
			.verifyComplete();
	}
}
