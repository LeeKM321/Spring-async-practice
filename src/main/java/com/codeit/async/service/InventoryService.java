package com.codeit.async.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final WebClient webClient;

    public Mono<Map> checkStock(String productId) {
        log.info("[{}] 재고 확인 시작: {}", Thread.currentThread().getName(), productId);

        return webClient.get()
                .uri("/mock/inventory/check/{productId}", productId)
                .retrieve()
                .bodyToMono(Map.class) // 전달된 응답을 Map으로 바꿔주세요. (DTO 있으시면 그걸로 받으셔도 됩니다.)
                .doOnSuccess(result -> {
                    log.info("[{}] 재고 확인 완료: {}", Thread.currentThread().getName(), result);
                });
    }

}










