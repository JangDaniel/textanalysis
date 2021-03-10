package com.insutil.textanalysis.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

@SpringBootTest
public class EvaluationHandlerTest {
	@Autowired
	private EvaluationHandler evaluationHandler;

	@Test
	void getScriptMatches() {
		evaluationHandler.getScriptMatches(8L)
			.doOnNext(System.out::println)
			.as(StepVerifier::create)
			.thenConsumeWhile(scriptMatch -> scriptMatch.getId() != null)
			.verifyComplete();
	}
}
