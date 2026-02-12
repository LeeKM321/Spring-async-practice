package com.codeit.async.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class PaymentService {

    @Async("paymentExecutor")
    public CompletableFuture<Boolean> processPayment(String userId, int amount) {
        String threadName = Thread.currentThread().getName();
        log.info("[{}] 결제 처리 시작: userId={}, amount={}", threadName, userId, amount);

        sleep(3000); // 결제 처리시간

        // 실제로는 PG사 API 호출 등...
        boolean success = true;
        log.info("[{}] 결제 처리 완료!: userId={}, success={}", threadName, userId, success);

        return CompletableFuture.completedFuture(success);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted", e);
        }
    }

}
