package com.codeit.async.controller;

import com.codeit.async.model.Coffee;
import com.codeit.async.model.PaymentResult;
import com.codeit.async.service.CoffeeService;
import com.codeit.async.service.NotificationService;
import com.codeit.async.service.OrderService;
import com.codeit.async.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final CoffeeService coffeeService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

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

    @GetMapping("/full/{type}")
    public CompletableFuture<String> fullOrder(@PathVariable String type) {
        log.info("=== 전체 주문 프로세스 시작 ===");

        // 1. 커피 제조
        CompletableFuture<Coffee> coffeeFuture = coffeeService.makeCoffeeAsync(type);

        // 2. 결제 처리
        CompletableFuture<Boolean> paymentFuture = paymentService.processPayment("user123", 4500);

        // 3. 알림 발송 (void)
        notificationService.notifyCustomer(type);

        // 4. 커피와 결제가 모두 완료되면 응답
        return coffeeFuture.thenCombine(paymentFuture, (coffee, paymentResult) -> {
            log.info("=== 전체 주문 프로세스 완료! ===");
            return String.format("주문 완료: %s (결제: %s)", coffee.getType(), paymentResult ? "성공":"실패");
        });
    }

    @GetMapping("/load-test")
    public String loadTest() {
        long start = System.currentTimeMillis();
        log.info("==== 부하 테스트 시작: 100개 주문 ====");

        // 100개 동시 주문!
        List<CompletableFuture<Coffee>> futures = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            futures.add(coffeeService.makeCoffeeAsync("아메리카노"));
        }

        // 모두 완료 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long duration = System.currentTimeMillis() - start;
        log.info("==== 부하 테스트 완료: {}ms ====", duration);

        return String.format("100개 주문 완료: %dms", duration);
    }

    @GetMapping("/event-test")
    public String eventTest(@RequestParam(defaultValue = "4500") int price) {
        Long orderId = orderService.createOrderWithEvent("user123", "아메리카노", price);
        return "주문 생성 (이벤트): " + orderId;
    }

    @GetMapping("/db")
    public String transactionTest(@RequestParam(defaultValue = "4500") int price) {
        Long orderId = orderService.createOrderWithDB("user123", "아메리카노", price);
        return "주문 생성 (이벤트): " + orderId;
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> request) {
        String orderId = "ORD-" + System.currentTimeMillis();
        String customerName = (String) request.get("customerName");
        Integer amount = (Integer) request.get("amount");

        log.info("주문 접수: {} - 고객: {}, 금액: {}원", orderId, customerName, amount);

        // 비동기 결제 처리
        paymentService.processPayment(orderId, customerName, amount);

        return ResponseEntity.ok(Map.of("orderId", orderId, "status", "PROCESSING"));
    }

    @PostMapping("/safe")
    public ResponseEntity<?> createOrderSafely(@RequestBody Map<String, Object> request) {
        String orderId = "ORD-" + System.currentTimeMillis();
        String customerName = (String) request.get("customerName");
        Integer amount = (Integer) request.get("amount");

        log.info("주문 접수: {} - 고객: {}, 금액: {}원", orderId, customerName, amount);

        // 비동기 결제 처리
        CompletableFuture<PaymentResult> paymentFuture
                = paymentService.processPaymentWithResult(orderId, customerName, amount);

        try {
            PaymentResult result = paymentFuture.get(5, TimeUnit.SECONDS);

            if (result.isSuccess()) {
                return ResponseEntity.ok(
                        Map.of(
                                "orderId", orderId,
                                "status", "SUCCESS",
                                "message", result.getMessage()
                        )
                );
            } else {
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(
                        Map.of(
                                "orderId", orderId,
                                "status", "FAILED",
                                "message", result.getMessage()
                        )
                );
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }



    }






}















