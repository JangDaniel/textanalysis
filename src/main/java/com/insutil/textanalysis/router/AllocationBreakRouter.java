package com.insutil.textanalysis.router;

import com.insutil.textanalysis.handler.AllocationBreakHandler;
import com.insutil.textanalysis.handler.EvaluationAllocationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AllocationBreakRouter {
	@Bean
	public RouterFunction<ServerResponse> allocationBreakRoute(AllocationBreakHandler handler) {
		return RouterFunctions.route()
			.GET("/api/allocation/break/{date}/{evaluatorId}", handler::findAllByDateAndEvaluatorId)
			.POST("/api/allocation/break", handler::save)
			.PUT("/api/allocation/break/{id}", handler::update)
			.build();
	}
}
