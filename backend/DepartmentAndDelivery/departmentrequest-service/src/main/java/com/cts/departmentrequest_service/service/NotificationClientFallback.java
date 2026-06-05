package com.cts.departmentrequest_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.cts.departmentrequest_service.dto.NotificationRequestDto;

@Component
public class NotificationClientFallback implements NotificationClient {

    private static final Logger log = 
        LoggerFactory.getLogger(NotificationClientFallback.class);

    @Override
    public void sendNotification(NotificationRequestDto dto) {
        log.warn("Notification service unavailable — " +
                 "failed to send notification for referenceId: {}",
                 dto.getReferenceId());
    }
}