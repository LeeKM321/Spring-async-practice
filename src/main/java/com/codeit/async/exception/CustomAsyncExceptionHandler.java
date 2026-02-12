package com.codeit.async.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 *  비동기 작업 중 발생한 예외를 처리하는 핸들러
 *  void를 반환하는 @Async 메서드에서 발생한 예외를 캐치합니다.
 *  CompletableFuture 등을 리턴하는 @Async 메서드는 .exceptionally() or .handle() 등을 이용해 자체 처리해 주시면 됩니다.
 */
@Slf4j
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {

        log.error("======================================================");
        log.error("비동기 작업 중 예외 발생!");
        log.error("예외 메세지: {}", ex.getMessage());
        log.error("메서드 이름: {}", method.getName());
        log.error("파라미터: {}", Arrays.toString(params));
        log.error("======================================================");

        // 메신저나 이메일로 알림 발송
        // 모니터링 시스템에 기록
        // 재시도 or 롤백
        // 관리자 대시보드에 표시....

    }
}














