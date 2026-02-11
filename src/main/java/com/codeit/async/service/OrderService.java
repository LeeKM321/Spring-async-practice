package com.codeit.async.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final NotificationService notificationService;

    public void processOrder(String type) {
        log.info("[{}] 주문 처리 시작: ", Thread.currentThread().getName());

        // 자가 호출 문제를 조심해야 합니다. (같은 클래스 내의 @Async 메서드를 호출해도 비동기 안먹습니다.)
        // this를 통해 호출하는 메서드는 프록시 객체를 거치지 않기 때문에 스레드 할당이 안됩니다.
        notificationService.notifyCustomer(type);

        log.info("[{}] 주문 처리 완료: ", Thread.currentThread().getName());
    }




}
