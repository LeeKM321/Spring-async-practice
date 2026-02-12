package com.codeit.async.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

// 주문 취소 이벤트
@Getter
@RequiredArgsConstructor
public class OrderCancelledEvent {

    private final Long orderId;
    private final String reason;
    private final LocalDateTime occuredAt =  LocalDateTime.now();

}
