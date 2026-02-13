package com.codeit.async.service;

import com.codeit.async.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
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

    public Mono<Map<String, Object>> checkStockSafe(String productId) {
         return webClient.get()
                 .uri("/mock/inventory/check-error/{productId}", productId)
                 .retrieve()
                 .onStatus(
                         status -> status.is4xxClientError() || status.is5xxServerError(),
                         response -> {
                             log.error("API 에러 발생: {}", response.statusCode());
                             return Mono.error(new RuntimeException("재고 확인 실패: " + response.statusCode()));
                         }
                 )
                 .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                 })
                 .onErrorReturn(error -> {
                     log.error("재고 확인 중 예외 발생", error);
                     return true; // 모든 에러를 여기서 처리하겠다.
                 }, Map.of(
                         "productId", productId,
                         "inStock", false,
                         "error", true,
                         "message", "재고 확인 실패"
                 ));
    }

    public Mono<Map<String, Object>> checkStockWithRetry(String productId) {
        return webClient.get()
                .uri("/mock/inventory/check-error/{productId}", productId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
//                .retry(3)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .maxBackoff(Duration.ofSeconds(10))
                        .doBeforeRetry(signal ->
                                log.warn("재시도 #{} - 사유: {}", signal.totalRetries() + 1, signal.failure().getMessage())
                        )
                )
                .onErrorResume(ex -> {
                    log.error("3번 재시도했으나 최종 실패! 원인: {}", ex.getMessage());

                    // 선택지 1: 비상용 기본 데이터 반환
                    return Mono.just(Map.of(
                            "productId", productId,
                            "stock", "UNKNOWN",
                            "message", "잠시 후 다시 시도해 주세요."
                    ));

                    // 선택지 2: 비즈니스 커스텀 예외로 바꿔서 던지기
//                    return Mono.error(new RuntimeException("외부 연동 실패!", ex));
                });
    }


    public Mono<PostResponseDto> createPostToJsonPlaceHolder(PostRequestDto requestDto) {
        WebClient client = WebClient.create("https://jsonplaceholder.typicode.com");

        return client.post()
                .uri("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(PostResponseDto.class);

    }

    public Mono<UserDashboard> getDashboard(int userId) {
        WebClient client = WebClient.create("https://jsonplaceholder.typicode.com");

        // 사용자 정보 요청
        Mono<UserResponse> userMono = client.get()
                .uri("/users/{id}", userId)
                .retrieve()
                .bodyToMono(UserResponse.class)
                // 메인 스레드는 비즈니스 로직만 수행하고, API 호출 및 데이터 처리는 boundedElastic에게 넘기겠다.
                .subscribeOn(Schedulers.boundedElastic());

        // 할 일 정보 요청
        Mono<TodoResponse> todoMono = client.get()
                .uri("/todos/{userId}", userId)
                .retrieve()
                .bodyToMono(TodoResponse.class)
                .subscribeOn(Schedulers.boundedElastic());

        return Mono.zip(userMono, todoMono, (user, todo) -> {

            String companyName = user.company().name();
            String status = todo.completed() ? "완료" : "진행 중";

            return new UserDashboard(
                    user.name(),
                    user.email(),
                    companyName,
                    todo.title(),
                    status
            );
        });


    }



}










