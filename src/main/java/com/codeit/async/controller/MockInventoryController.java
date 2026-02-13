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

}















