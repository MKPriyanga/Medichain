package com.medichain.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequestDTO {

    @NotNull(message = "userId is required")
    private Long userId;

    private Long referenceId;    

    @NotBlank(message = "message is required")
    private String message;

    @NotBlank(message = "category is required")
    private String category;     
}