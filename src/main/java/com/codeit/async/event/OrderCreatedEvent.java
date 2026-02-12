package com.codeit.async.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

// 주문 생성 이벤트
@Getter
@RequiredArgsConstructor
public class OrderCreatedEvent {

    private final Long orderId;
    private final String userId;
    private final String coffeeType;
    private final int price;
    private final LocalDateTime occuredAt =  LocalDateTime.now();

}
