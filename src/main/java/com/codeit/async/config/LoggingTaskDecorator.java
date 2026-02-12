package com.codeit.async.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

@Slf4j
public class LoggingTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        // 현재 스레드의 MDC 전체를 복사
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();

        log.info("Task Decorator - MDC 컨텍스트 캡처: {}", mdcContext);

        return () -> {
          try {
              // 비동기 스레드에 MDC 복원
              if (mdcContext != null) {
                  MDC.setContextMap(mdcContext);
                  log.info("Task Decorator - MDC 컨텍스트 전파 완료");
              }
              runnable.run();
          } finally {
              MDC.clear();
          }
        };

    }

}









