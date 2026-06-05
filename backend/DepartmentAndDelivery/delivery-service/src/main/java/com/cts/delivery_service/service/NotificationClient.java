package com.cts.delivery_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.cts.delivery_service.dto.NotificationRequestDto;

@FeignClient(
    name = "notification-service",
    fallback = NotificationClientFallback.class
)
public interface NotificationClient {

    @PostMapping("/internal/notifications")
    void sendNotification(@RequestBody NotificationRequestDto dto);
}
