package com.codeit.async.config;

import com.codeit.async.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

/**
 * 모든 컨텍스트를 전파하는 통합 Decorator
 */
@Slf4j
public class ContextPropagatingTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        // 메인 스레드의 모든 컨텍스트 캡처!
        String currentUser = UserContext.getCurrentUser();
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();

        log.debug("컨텍스트 캡처 - User: {}, MDC: {}", currentUser, mdcContext);

        return () -> {
            try {
                // 비동기 스레드에 모든 컨텍스트 복원!
                UserContext.setCurrentUser(currentUser);

                if (mdcContext != null) {
                    MDC.setContextMap(mdcContext);
                }

                log.debug("컨텍스트 전파 완료 - Thread: {}, User: {}, TraceId: {}",
                        Thread.currentThread().getName(),
                        currentUser,
                        MDC.get("traceId"));

                runnable.run();

            } finally {
                // 모든 컨텍스트 정리!
                UserContext.clear();
                MDC.clear();
                log.debug("컨텍스트 정리 완료");
            }
        };
    }
}
















