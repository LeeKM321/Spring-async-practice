package com.codeit.async.controller;

import com.codeit.async.config.AsyncUtil;
import com.codeit.async.context.UserContext;
import com.codeit.async.model.Coffee;
import com.codeit.async.model.PaymentResult;
import com.codeit.async.service.CoffeeService;
import com.codeit.async.service.NotificationService;
import com.codeit.async.service.OrderService;
import com.codeit.async.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
    private final AsyncUtil asyncUtil;

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
            return String.format("주문 완료: %s (결제: %s)", coffee.getType(), paymentResult ? "성공" : "실패");
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
    public CompletableFuture<ResponseEntity<Map<String, String>>> createOrderSafely(@RequestBody Map<String, Object> request) {
        String orderId = "ORD-" + System.currentTimeMillis();
        String customerName = (String) request.get("customerName");
        Integer amount = (Integer) request.get("amount");

        log.info("주문 접수: {} - 고객: {}, 금액: {}원", orderId, customerName, amount);

        // 비동기 결제 처리
//        return paymentService.processPaymentWithResult(orderId, customerName, amount)
        // 재시도 로직
//                .handle((res, ex) -> {
//                    // 예외 발생 시 재시도 로그를 찍고 다시 서비스를 호출
//                    if (ex != null) {
//                        log.warn("1차 결제 실패! 재시도를 수행합니다. (사유: {})", ex.getMessage());
//                        return paymentService.processPaymentWithResult(orderId, customerName, amount);
//                    }
//                    // 성공 시 원래 결과를 그대로 다시 Future로 감싸서 넘깁니다.
//                    return CompletableFuture.completedFuture(res);
//                })
//
//                // 비동기 작업의 결과를 받아서 또다른 비동기 작업을 연결할 때
//                // thenApply(): 다음 단계가 값만 바꾸는 단순 작업일 때
//                // 여기서 thenCompose를 사용한 이유: 실패한 현재의 Future를 버리고, 새로 생성된 Future를 메인 흐름에 끼워넣는 과정 자체가
//                // 구조적으로 또 다른 비동기 작업의 연결이라고 해석
//                .thenCompose(future -> future)
                return asyncUtil.retry(
                        () -> paymentService.processPaymentWithResult(orderId, customerName, amount), 2
                )
                .orTimeout(10, TimeUnit.SECONDS)
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        return ResponseEntity.ok(Map.of(
                                "orderId", orderId,
                                "status", "SUCCESS",
                                "message", result.getMessage()
                        ));
                    } else {
                        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                                .body(
                                        Map.of(
                                                "orderId", orderId,
                                                "status", "FAILED",
                                                "message", result.getMessage()
                                        )
                                );
                    }
                })
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause();
                    if (ex instanceof TimeoutException || cause instanceof TimeoutException) {
                        log.error("결제처리 시간 초과");
                        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                                .body(Map.of("status", "TIMEOUT", "message", "결제 시간이 초과되었습니다."));
                    }

                    log.error("타임아웃 이외의 예외: {}", cause.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("status", "BAD_REQUEST", "message", cause.getMessage()));
                });

    }

    @PostMapping("/with-user")
    public ResponseEntity<?> createOrderWithUser(@RequestBody Map<String, Object> request) {
        // 사용자 정보 설정 (실제로는 @AuthenticationPrincipal -> JWT 토큰에서 추출)
        UserContext.setCurrentUser("admin@codeit.com");

        String orderId = "ORD-" + System.currentTimeMillis();
        String customerName = (String) request.get("customerName");
        Integer amount = (Integer) request.getOrDefault("amount", 4000);

        log.info("주문 접수 - 요청자: {}", UserContext.getCurrentUser());

        // 비동기 알림 발송 (UserContext가 전파됨)
        notificationService.sendOrderConfirmation(orderId, customerName);

        UserContext.clear();  // 요청 처리 후 정리

        return ResponseEntity.ok(Map.of("orderId", orderId, "status", "PROCESSING"));
    }

    @PostMapping("/with-trace")
    public ResponseEntity<?> createOrderWithTrace(@RequestBody Map<String, Object> request) {
        // 추적 ID 생성 (실제로는 요청 헤더나 필터에서 설정 -> 여러분들 스프린트 미션, 프로젝트에서는 requestId로 세팅)
        String traceId = "TRACE-" + System.currentTimeMillis();
        MDC.put("traceId", traceId);
        UserContext.setCurrentUser("admin@codeit.com");

        log.info("주문 접수 [traceId: {}]", traceId);

        String orderId = "ORD-" + System.currentTimeMillis();
        String customerName = (String) request.get("customerName");
        Integer amount = (Integer) request.getOrDefault("amount", 4000);

        // 비동기 작업들 (MDC와 UserContext가 모두 전파됨!)
        coffeeService.makeCoffeeAsync("아메리카노");
        notificationService.sendOrderConfirmation(orderId, customerName);
        paymentService.processPayment(orderId, customerName, amount);

        // 정리
        MDC.clear();
        UserContext.clear();

        return ResponseEntity.ok(Map.of(
                "orderId", orderId,
                "traceId", traceId,
                "status", "PROCESSING"
        ));
    }




}















