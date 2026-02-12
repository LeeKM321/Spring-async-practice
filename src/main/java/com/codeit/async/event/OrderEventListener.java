package com.codeit.async.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    /**
     * 1. 알림 발송 리스너
     */
    @EventListener
    @Async("notificationExecutor")
    public void handleOrderCreatedAsync(OrderCreatedEvent event) {
        String threadName = Thread.currentThread().getName();
        log.info("[{}] 주문 생성 이벤트 수신 (비동기): orderId={}", threadName, event.getOrderId());

        // 알림 발송 시뮬레이션
        sleep(3000);  // 2초 걸림

        log.info("[{}] 알림 발송 완료: orderId={}", threadName, event.getOrderId());
    }

    /**
     * 2. 포인트 적립 리스너
     */
    @EventListener
    @Async
    public void addPoints(OrderCreatedEvent event) {
        String threadName = Thread.currentThread().getName();
        log.info("[{}] 포인트 적립 시작: userId={}", threadName, event.getUserId());

        int points = event.getPrice() / 10;  // 10% 적립
        sleep(1000);  // 1초 걸림

        log.info("[{}] 포인트 적립 완료: {}points", threadName, points);
    }

    /**
     * 3. 통계 수집 리스너
     */
    @EventListener
    @Async
    public void collectStats(OrderCreatedEvent event) {
        String threadName = Thread.currentThread().getName();
        log.info("[{}] 통계 수집 시작: orderId={}", threadName, event.getOrderId());

        sleep(500);  // 0.5초 걸림

        log.info("[{}] 통계 수집 완료", threadName);
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










