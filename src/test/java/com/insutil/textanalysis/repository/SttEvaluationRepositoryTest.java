package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.dto.EvaluationRateData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

	@Test
	void findAllByLimit() {
		repository.findAll()
			.doOnNext(sttEvaluation -> System.out.print(sttEvaluation.getId() + " "))
			.as(StepVerifier::create)
			.thenConsumeWhile(sttEvaluation -> sttEvaluation.getId() != null)
			.verifyComplete();

		repository.findAllByLimit(0, 10)
			.doOnNext(sttEvaluation -> System.out.print(sttEvaluation.getId() + " "))
			.as(StepVerifier::create)
			.thenConsumeWhile(sttEvaluation -> sttEvaluation.getId() != null)
			.verifyComplete();

		repository.findAllByLimit(10, 10)
			.doOnNext(sttEvaluation -> System.out.print(sttEvaluation.getId() + " "))
			.as(StepVerifier::create)
			.thenConsumeWhile(sttEvaluation -> sttEvaluation.getId() != null)
			.verifyComplete();
	}

	@Test
	void getEvaluationRateDataTest() {
		/*List<EvaluationRateData> evaluationRateDataList = repository.getEvaluationRateData().collectList().block();
		evaluationRateDataList.forEach(System.out::println);*/
		LocalDateTime fromLocalDateTime = LocalDateTime.of(2021, 04, 02, 00, 00, 00);
		LocalDateTime toLocalDateTime = LocalDateTime.of(2021, 04, 02, 23, 59, 59);

		repository.getEvaluationRateData(fromLocalDateTime, toLocalDateTime)
				.doOnNext(System.out::println)
				.as(StepVerifier::create)
				.thenConsumeWhile(data -> data.getUserId() != null)
				.verifyComplete();

		String value = "0.50";
		System.out.println(Math.round(Float.valueOf(value) * 100));
	}
}
