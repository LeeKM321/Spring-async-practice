package com.codeit.async.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    // 커피 제조용 Executor
    @Bean(name = "coffeeExecutor")
    public ThreadPoolTaskExecutor coffeeExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(10); // 기본 스레드 수
        executor.setMaxPoolSize(20); // 최대 스레드 수
        executor.setQueueCapacity(100); // 큐 크기
        executor.setKeepAliveSeconds(60); // 유휴 스레드 유지 시간

        // 스레드 이름 (로그에서 구분하기 쉽게)
        executor.setThreadNamePrefix("coffee-");

        // 거부 정책 (ThreadPoolTaskExecutor의 한계치를 넘는 요청이 들어올 경우 나머지는 메인 스레드가 실행하라)
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 종료 시 대기
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();

        log.info("coffeeExecutor initialize: core={}, max={}, queue={}",
                executor.getCorePoolSize(),
                executor.getMaxPoolSize(),
                executor.getQueueCapacity());

        return executor;
    }

    @Bean(name = "notificationExecutor")
    public ThreadPoolTaskExecutor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("notification-");

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());

        executor.initialize();
        return executor;
    }

    @Bean(name = "paymentExecutor")
    public ThreadPoolTaskExecutor paymentExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("payment-");

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(120);

        executor.initialize();
        return executor;
    }


}















