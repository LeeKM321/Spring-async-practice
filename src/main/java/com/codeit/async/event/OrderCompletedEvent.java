package com.codeit.async.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

// 주문 완료 이벤트
@Getter
@RequiredArgsConstructor
public class OrderCompletedEvent {

    private final Long orderId;
    private final String userId;
    private final LocalDateTime occuredAt =  LocalDateTime.now();

}
