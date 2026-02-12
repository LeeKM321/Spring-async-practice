package com.codeit.async.service;

import com.codeit.async.entity.Order;
import com.codeit.async.event.OrderCreatedEvent;
import com.codeit.async.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderRepository orderRepository;

    @Transactional
    public Long createOrderWithDB(String userId, String coffeeType, int price) {
        String threadName = Thread.currentThread().getName();
        log.info("[{}] 주문 생성 시작: userId={}, coffeeType={}", threadName, userId, coffeeType);

        // 1. DB에 주문 저장
        Order order = new Order(userId, coffeeType, price);
        Order savedOrder = orderRepository.save(order);

        log.info("[{}] 주문 DB 저장 완료: orderId={} (아직 커밋 안됨!)", threadName, savedOrder.getId());

        // 2. 이벤트 발행!
        OrderCreatedEvent event = new OrderCreatedEvent(savedOrder.getId(), userId, coffeeType, price);
        log.info("[{}] 이벤트 발행 완료! (아직 커밋 안됨!)", threadName);
        eventPublisher.publishEvent(event);

        sleep(1000); // 트랜잭션 메서드가 종료될 때까지 약간의 시간이 소요된다 가정

        log.info("[{}] createOrderWithDB 종료 (이제 커밋됨!)", threadName);
        return savedOrder.getId();
    }

    // 이벤트를 발행하는 주문 생성
    public Long createOrderWithEvent(String userId, String coffeeType, int price) {
        String threadName = Thread.currentThread().getName();
        log.info("[{}] 주문 생성 시작: userId={}, coffeeType={}", threadName, userId, coffeeType);

        // 1. 주문 ID 생성 (실제로는 DB에 저장하고 ID를 받아옴)
        Long orderId = System.currentTimeMillis();

        // 2. 이벤트 발행!
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, userId, coffeeType, price);
        eventPublisher.publishEvent(event);

        log.info("[{}] 이벤트 발행 완료!", threadName);
        return orderId;
    }




    public void processOrder(String type) {
        log.info("[{}] 주문 처리 시작: ", Thread.currentThread().getName());

        // 자가 호출 문제를 조심해야 합니다. (같은 클래스 내의 @Async 메서드를 호출해도 비동기 안먹습니다.)
        // this를 통해 호출하는 메서드는 프록시 객체를 거치지 않기 때문에 스레드 할당이 안됩니다.
        notificationService.notifyCustomer(type);

        log.info("[{}] 주문 처리 완료: ", Thread.currentThread().getName());
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
