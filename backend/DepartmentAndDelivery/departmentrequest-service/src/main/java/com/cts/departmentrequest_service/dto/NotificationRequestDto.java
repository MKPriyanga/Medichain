package com.cts.departmentrequest_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDto {

    private Long userId;
    private Long referenceId;
    private String message;
    private String category;
}
