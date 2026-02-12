package com.codeit.async.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConditionalEventListener {

    @EventListener(condition = "#event.price >= 100000")
    @Async
    public void handleExpensiveOrder(OrderCreatedEvent event) {
        log.info("💰 고가 주문 감지! orderId={}, price={}원", event.getOrderId(), event.getPrice());

        // VIP 전용 알림 발송
        // 특별 포인트 지급
        // 할인 쿠폰 발급 ....
    }

    /**
     * VIP 고객 주문만 처리
     */
    @EventListener(condition = "#event.userId.startsWith('VIP')")
    @Async
    public void handleVipOrder(OrderCreatedEvent event) {
        log.info("VIP 주문: userId={}", event.getUserId());
        // VIP 혜택 제공
    }

    /**
     * 고가 + 아메리카노 조합 (AND 조건)
     */
    @EventListener(condition = "#event.price >= 10000 and #event.coffeeType == '아메리카노'")
    @Async
    public void handleExpensiveAmericano(OrderCreatedEvent event) {
        log.info("고가 아메리카노 주문!", event.getOrderId());
    }

    /**
     * 첫 주문인 경우 (isFirstOrder 필드가 있다고 가정)

    @EventListener(condition = "#event.isFirstOrder == true")
    @Async
    public void handleFirstOrder(OrderCreatedEvent event) {
        log.info("첫 주문 축하! userId={}", event.getUserId());
        // 첫 주문 쿠폰 발급
    }
     */

}
