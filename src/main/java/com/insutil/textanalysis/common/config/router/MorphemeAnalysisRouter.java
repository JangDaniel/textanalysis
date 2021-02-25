package com.insutil.textanalysis.common.config.router;

import com.insutil.textanalysis.handler.MorphemeAnalysisHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class MorphemeAnalysisRouter {
    @Bean
    public RouterFunction<ServerResponse> categoryRoute(MorphemeAnalysisHandler handler) {
        return RouterFunctions.route()
                .POST("/api/morpheme", handler::analysisMorpheme)
                .build();
    }
}
