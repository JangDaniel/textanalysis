package com.insutil.textanalysis.router;

import com.insutil.textanalysis.handler.EvaluationAllocationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class EvaluationAllocationRouter {
	@Bean
	public RouterFunction<ServerResponse> evaluationAllocationRoute(EvaluationAllocationHandler handler) {
		return RouterFunctions.route()
			.GET("/api/evaluation/allocation/user", handler::findAllEvaluators)
			.GET("/api/evaluation/allocation/{date}", handler::findByDate)
			.POST("/api/evaluation/allocation", handler::save)
			.PUT("/api/evaluation/allocation/{id}", handler::update)
			.build();
	}
}
