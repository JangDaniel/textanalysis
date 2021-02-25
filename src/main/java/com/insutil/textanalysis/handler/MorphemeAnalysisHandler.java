package com.insutil.textanalysis.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class MorphemeAnalysisHandler {

    private final Executor executor = Executors.newFixedThreadPool(1);

    public Mono<ServerResponse> analysisMorpheme(ServerRequest request) {
        return request.bodyToMono(Integer[].class)
                .publishOn(Schedulers.newParallel("test"))
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
            return true;
        }, executor);
        return true;
    }
}
