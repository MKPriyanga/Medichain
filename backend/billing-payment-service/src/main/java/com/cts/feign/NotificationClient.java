package com.cts.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.extern.slf4j.Slf4j;

@FeignClient(
    name = "notification-service",
    path = "/internal/notifications",
    fallback = NotificationClient.NotificationFallback.class
)
public interface NotificationClient {

    @PostMapping
    void sendNotification(@RequestBody NotificationRequestDto dto);

    @Component
    @Slf4j
    class NotificationFallback implements NotificationClient {
        @Override
        public void sendNotification(NotificationRequestDto dto) {
            log.warn("Notification service unavailable — billing notification skipped for userId={}",
                    dto.getUserId());
        }
    }
}