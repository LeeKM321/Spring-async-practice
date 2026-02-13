package com.codeit.async;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableRetry
public class SpringAsyncPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAsyncPracticeApplication.class, args);
    }

}
