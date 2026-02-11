package com.codeit.async.service;

import com.codeit.async.model.Coffee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class CoffeeService {

    public Coffee makeCoffee(String type) {
        String threadName = Thread.currentThread().getName();
        log.info("[{}] 커피 제조 시작: {}", threadName, type);

        sleep(5000);

        Coffee coffee = new Coffee(type);
        log.info("[{}] 커피 제조 완료: {}",  threadName, type);

        return coffee;
    }

    // 비동기 메서드
    @Async
    public CompletableFuture<Coffee> makeCoffeeAsync(String type) {
        String threadName = Thread.currentThread().getName();
        log.info("[{}] 커피 제조 시작: {}", threadName, type);

        sleep(5000);

        Coffee coffee = new Coffee(type);
        log.info("[{}] 커피 제조 완료: {}",  threadName, type);

        return CompletableFuture.completedFuture(coffee);
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













