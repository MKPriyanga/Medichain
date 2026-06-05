package com.medichain.iam.client;

import com.medichain.iam.dto.NotificationRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.extern.slf4j.Slf4j;

@FeignClient(
    name = "notification-service",
    path = "/internal/notifications",
    fallback = NotificationFeignClient.NotificationFallback.class
)
public interface NotificationFeignClient {

    @PostMapping
    void sendNotification(@RequestBody NotificationRequestDTO dto);

    @Component
    @Slf4j
    class NotificationFallback implements NotificationFeignClient {
        @Override
        public void sendNotification(NotificationRequestDTO dto) {
            log.warn("Notification service unavailable - notification skipped for userId={}",
                    dto.getUserId());
        }
    }
}