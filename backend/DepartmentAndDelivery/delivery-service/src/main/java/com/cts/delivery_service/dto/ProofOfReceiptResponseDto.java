package com.cts.delivery_service.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProofOfReceiptResponseDto {

    private Integer proofId;
    private Integer deliveryId;
    private Integer departmentId;
    private LocalDateTime receivedAt;
    private String fileUri;
    private String status;
}
