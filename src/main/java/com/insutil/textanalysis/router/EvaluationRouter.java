package com.insutil.textanalysis.router;

import com.insutil.textanalysis.handler.EvaluationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class EvaluationRouter {
	@Bean
	public RouterFunction<ServerResponse> evaluationRoute(EvaluationHandler handler) {
		return RouterFunctions.route()
			.GET("/api/evaluation", handler::findAll)
			.GET("/api/evaluation/query", handler::findAllByQuery)
			.GET("/api/evaluation/count", handler::getTotalCountByQuery)
			.GET("/api/evaluation/{id}", handler::findSttEvaluationById)
//			.GET("/api/evaluation/date/{callDate}", handler::findCallEvaluationsByDate)
//			.POST("/api/evaluation", handler::saveCallEvaluation)// CallEvaluation 정보는 front-end에서 생성하지 않는다
			.PUT("/api/evaluation/{id}", handler::updateSttEvaluation)
			.GET("/api/evaluation/stt/{sttEvaluationId}", handler::findSttEvaluationSummaries)
//			.GET("/api/evaluation/stt/{sttEvaluationId}/evaluations", handler::findSttEvaluationDetails)
			.build();
	}
}
