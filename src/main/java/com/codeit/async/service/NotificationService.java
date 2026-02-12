package com.codeit.async.service;

import com.codeit.async.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    @Async("notificationExecutor")
    public void notifyCustomer(String type) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("[{}] 고객 알림 발송", Thread.currentThread().getName());
    }

    @Async("notificationExecutor")
    public void sendOrderConfirmation(String orderId, String customerName) {
        String currentUser = UserContext.getCurrentUser();
        log.info("[{}] 주문 확인 알림 발송 - 주문번호={}, 고객={}, 요청자={}",
                Thread.currentThread().getName(), orderId, customerName, currentUser);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("[{}] 고객 알림 발송", Thread.currentThread().getName());
    }

}











