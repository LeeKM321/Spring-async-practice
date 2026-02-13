package com.codeit.async.service;

import com.codeit.async.exception.PaymentException;
import com.codeit.async.model.PaymentResult;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class PaymentService {

    @Async("paymentExecutor")
    public void processPayment(String orderId, String customerName, int amount) {
        String threadName = Thread.currentThread().getName();
        log.info("[{}] 결제 처리 시작: 주문번호={}, 고객={}, amount={}", threadName, orderId, customerName, amount);

        sleep(2000); // 결제 처리시간

        if (amount > 100000) {
            throw new IllegalArgumentException("결제 금액이 한도를 초과했습니다." + amount + "원");
        }

        log.info("[{}] 결제 처리 완료!: 주문번호={}", threadName, orderId);
    }

    @Async("paymentExecutor")
    public CompletableFuture<Boolean> processPayment(String userId, int amount) {
        String threadName = Thread.currentThread().getName();
        log.info("[{}] 결제 처리 시작: userId={}, amount={}", threadName, userId, amount);

        sleep(2000); // 결제 처리시간

        if (amount > 100000) {
            throw new IllegalArgumentException("결제 금액이 한도를 초과했습니다." + amount + "원");
        }

        // 실제로는 PG사 API 호출 등...
        boolean success = true;
        log.info("[{}] 결제 처리 완료!: userId={}, success={}", threadName, userId, success);

        return CompletableFuture.completedFuture(success);
    }


    @Retry(name = "paymentRetry", fallbackMethod = "fallbackPayment")
    @Async("paymentExecutor")
    public CompletableFuture<PaymentResult> processPaymentWithResult(String orderId, String customerName, int amount) {
        String threadName = Thread.currentThread().getName();
        log.info("[{}] 결제 처리 시작: 주문번호={}, 고객={}, amount={}", threadName, orderId, customerName, amount);

        sleep(2000); // 결제 처리시간

        if (amount > 100000) {
            throw new PaymentException("결제 금액이 한도를 초과했습니다." + amount + "원");
        }

        if (Math.random() < 0.1) {
            throw new PaymentException("네트워크 에러 시뮬레이션! (10% 확률)");
        }

        log.info("[{}] 결제 처리 완료!: 주문번호={}", threadName, orderId);

        return CompletableFuture.completedFuture(new PaymentResult(true, orderId, "결제 성공"));

    }

    // 메서드 이름이 fallbackMethod와 일치해야 함.
    // 리턴타입이 원본 메서드와 동일해야 함.
    // 파라미터는 원본 파라미터 + 마지막에 Throwable 타입을 전달받아야 함.
    public CompletableFuture<PaymentResult> fallbackPayment(String orderId, String customerName, int amount, Throwable e) {
        log.error("모든 재시도 실패... 주문번호: {}, 최종 원인: {}", orderId, e.getMessage());

//        return CompletableFuture.failedFuture(e); -> 컨트롤러 쪽의 exceptionally 동작
        return CompletableFuture.completedFuture(new PaymentResult(false, orderId, e.getMessage()));
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
