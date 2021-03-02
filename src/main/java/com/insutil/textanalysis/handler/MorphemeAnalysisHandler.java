package com.insutil.textanalysis.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.concurrent.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class MorphemeAnalysisHandler implements InitializingBean, DisposableBean {

    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    public Mono<ServerResponse> analysisMorpheme(ServerRequest request) {
        return request.bodyToMono(Integer[].class)
                .map(this::analysis)
                .flatMap(data -> ServerResponse.ok().build());
    }

    public boolean analysis(Integer ...id) {
        CompletableFuture<Boolean> c = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
                Arrays.stream(id).forEach(i -> log.info("processing id = {}", i));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("complete.");
            return true;
        }, executor);
        return true;
    }

    @Override
    public void destroy() throws Exception {
        log.info("shutdown");
        executor.shutdownNow();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
