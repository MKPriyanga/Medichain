package com.medichain.notification.dto;

import com.medichain.notification.entity.NotificationCategory;
import com.medichain.notification.entity.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NotificationResponseDTO {

    private Long id;
    private Long userId;
    private Long referenceId;
    private String message;
    private NotificationCategory category;
    private NotificationStatus status;
    private LocalDateTime createdAt;
}