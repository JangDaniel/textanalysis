package com.insutil.textanalysis.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AllocationHandlerTest {
	@Autowired
	private EvaluationAllocationHandler allocationHandler;

	@org.junit.Test
	public void testRandomCount() {
		int max = 50;
		int count = 10;
		List<Long> randomList = ThreadLocalRandom.current().ints(0, max).distinct().limit(count).asLongStream().sorted().boxed().collect(Collectors.toList());
		System.out.println(randomList);
		assertEquals(10, randomList.size());
		assertEquals(10, Set.copyOf(randomList).size());
	}
}
