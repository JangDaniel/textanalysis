package com.insutil.textanalysis.router;

import com.insutil.textanalysis.handler.ScriptHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ScriptRouter {
	@Bean
	public RouterFunction<ServerResponse> scriptRoute(ScriptHandler handler) {
		return RouterFunctions.route()
			.GET("/api/script/product/{id}", handler::getScriptCriteriaByProductId)
			.GET("/api/script/criteria", handler::getScriptCriteria)
			.GET("/api/script/criteria/{id}", handler::getScriptCriterionById)
			.POST("/api/script/criteria", handler::saveScriptCriterion)
			.PUT("/api/script/criteria/{id}", handler::updateScriptCriterion)
			.POST("/api/script/detail", handler::saveScriptDetail)
			.PUT("/api/script/detail/{id}", handler::updateScriptDetail)
			.DELETE("/api/script/detail/{id}/{updateUser}", handler::disableScriptDetail)
			.build();
	}
}
