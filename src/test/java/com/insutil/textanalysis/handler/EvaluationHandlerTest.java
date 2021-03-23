package com.insutil.textanalysis.handler;

import com.insutil.textanalysis.model.CriterionEvaluationDetail;
import com.insutil.textanalysis.model.CriterionEvaluationSummary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;

@SpringBootTest
public class EvaluationHandlerTest {
	@Autowired
	private EvaluationHandler evaluationHandler;

		@Test
	void testEmptyMonoToFlux() {
		Flux.fromIterable(Arrays.asList("a", "b", "c"))
			.flatMap(this::getEmptyMono)
			.doOnNext(System.out::println)
			.as(StepVerifier::create)
//			.thenConsumeWhile(Objects::nonNull)
			.expectNextCount(0)
			.verifyComplete();
	}

	protected Mono<String> getEmptyMono(String text) {
		return Mono.empty();
	}

	@Test
	void testFindCriterionEvaluationDetailsRecursively() {
		CriterionEvaluationDetail detail1 = CriterionEvaluationDetail.builder()
			.scriptDetailId(1L)
			.script("Script detail 1")
			.build();
		CriterionEvaluationDetail detail2 = CriterionEvaluationDetail.builder()
			.scriptDetailId(2L)
			.script("Script detail 2")
			.build();
		CriterionEvaluationDetail detail3 = CriterionEvaluationDetail.builder()
			.scriptDetailId(3L)
			.script("Script detail 3")
			.build();
		CriterionEvaluationDetail detail4 = CriterionEvaluationDetail.builder()
			.scriptDetailId(4L)
			.script("Script detail 4")
			.build();
		List<CriterionEvaluationDetail> detailList1 = new ArrayList<>(Arrays.asList(detail1, detail2));
		List<CriterionEvaluationDetail> detailList2 = new ArrayList<>(Arrays.asList(detail3, detail4));
		CriterionEvaluationSummary root1 = CriterionEvaluationSummary.builder()
			.criterionId(1L)
			.criterionName("Root1")
			.criterionEvaluationDetails(new ArrayList<>())
			.build();
		CriterionEvaluationSummary main1 = CriterionEvaluationSummary.builder()
			.parentCriterionId(1L)
			.criterionId(10L)
			.criterionName("Main1")
			.criterionEvaluationDetails(new ArrayList<>())
			.build();
		CriterionEvaluationSummary sub1 = CriterionEvaluationSummary.builder()
			.parentCriterionId(10L)
			.criterionId(100L)
			.criterionName("Sub1")
			.criterionEvaluationDetails(detailList1)
			.build();
		CriterionEvaluationSummary sub2 = CriterionEvaluationSummary.builder()
			.parentCriterionId(10L)
			.criterionId(200L)
			.criterionName("Sub2")
			.criterionEvaluationDetails(detailList2)
			.build();
		Mono<CriterionEvaluationSummary> result = evaluationHandler.findCriterionEvaluationDetailsRecursively(root1, Flux.just(main1, sub1, sub2));
		result.doOnNext(summary -> System.out.println(summary.toString()))
			.as(StepVerifier::create)
			.expectNextMatches(summary -> summary.getCriterionEvaluationDetails().size() == 4)
			.verifyComplete();
	}
}
