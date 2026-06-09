package com.cts.delivery_service.service;

import org.springframework.stereotype.Component;
import com.cts.delivery_service.dto.NotificationRequestDto;

@Component
public class NotificationClientFallback implements NotificationClient {

    @Override
    public void sendNotification(NotificationRequestDto dto) {
        // Notification service unavailable — silently ignore
        // Do not fail the main operation
    }
}
