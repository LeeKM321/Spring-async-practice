package com.codeit.async.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/mock/inventory")
@Slf4j
public class MockInventoryController {


    @GetMapping("/check/{productId}")
    public Map<String, Object> checkStock(
            @PathVariable String productId,
            @RequestParam(defaultValue = "1000") int delay) throws InterruptedException {

        log.info("재고 확인 API 호출됨: productId={}, delay={}ms", productId, delay);

        // 네트워크 지연 시뮬레이션
        Thread.sleep(delay);

        return Map.of(
                "productId", productId,
                "inStock", true,
                "quantity", 100,
                "message", delay + "ms 후 응답"
        );
    }

    @GetMapping("/check-error/{productId}")
    public Map<String, Object> checkStockWithError(@PathVariable String productId) {
        log.info("에러 발생 API 호출됨: {}", productId);

        // 50% 확률로 에러!
        if (Math.random() < 0.5) {
            throw new RuntimeException("재고 시스템 오류!");
        }

        return Map.of("productId", productId, "inStock", true);
    }

}















