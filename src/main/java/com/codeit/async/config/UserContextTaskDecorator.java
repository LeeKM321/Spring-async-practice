package com.codeit.async.config;

import com.codeit.async.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskDecorator;

@Slf4j
public class UserContextTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        // 1. 현재 스레드(메인 스레드)의 사용자 정보 캡처
        String currentUser = UserContext.getCurrentUser();

        log.info("TaskDecorator - 사용자 정보 캡처: {}", currentUser);

        // 2. 새로운 Runnable을 반환
        return () -> {
            try {
                // 3. 비동기 스레드에 사용자 정보 설정
                UserContext.setCurrentUser(currentUser);
                log.info("TaskDecorator - 사용자 정보 전파 완료: {} -> {}", Thread.currentThread().getName(), currentUser);

                // 4. 실제 비동기 작업 수행
                runnable.run();
            } finally {
                // 5. 작업 완료 후 정리 (메모리 누수 방지, 전파 방지)
                UserContext.clear();
                log.info("TaskDecorator - 사용자 정보 정리 완료");
            }
        };

    }
}










