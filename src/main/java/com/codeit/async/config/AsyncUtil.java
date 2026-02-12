package com.codeit.async.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Component
@Slf4j
public class AsyncUtil {

    public <T> CompletableFuture<T> retry(Supplier<CompletableFuture<T>> action, int maxRetries) {
        return action.get()
                .handle((res, ex) -> {
                    if (ex == null) {
                        return CompletableFuture.completedFuture(res);
                    }

                    if (maxRetries > 0) {
                        log.warn("작업 실패! 재시도 남은 횟수: {}", maxRetries);
                        return retry(action, maxRetries - 1);
                    }

                    log.error("모든 재시도 실패! (원인: {})", ex.getMessage());
                    return CompletableFuture.<T>failedFuture(ex);
                })
                .thenCompose(f -> f);
    }

}
