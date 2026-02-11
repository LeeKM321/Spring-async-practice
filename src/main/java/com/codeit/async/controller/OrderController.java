package com.codeit.async.controller;

import com.codeit.async.model.Coffee;
import com.codeit.async.service.CoffeeService;
import com.codeit.async.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final CoffeeService coffeeService;
    private final OrderService orderService;

    @GetMapping("/sync/{type}")
    public Coffee orderCoffeeSync(@PathVariable String type) {
        long start = System.currentTimeMillis();
        log.info("=== 동기 주문 시작: {} ===", type);

        Coffee coffee = coffeeService.makeCoffee(type);

        long end = System.currentTimeMillis();
        log.info("=== 동기 주문 완료: {}ms ===", end - start);

        return coffee;
    }

    @GetMapping("/async/{type}")
    public CompletableFuture<Coffee> orderCoffeeAsync(@PathVariable String type) {
        long start = System.currentTimeMillis();
        log.info("=== 비동기 주문 시작: {} ===", type);

        CompletableFuture<Coffee> coffee = coffeeService.makeCoffeeAsync(type);

        long end = System.currentTimeMillis();
        log.info("=== 비동기 주문 완료: {}ms ===", end - start);

        return coffee;
    }

    @GetMapping("/test-self-invocation")
    public String testSelfInvocation() {
        orderService.processOrder("카페라떼");
        return "완료";
    }

}















