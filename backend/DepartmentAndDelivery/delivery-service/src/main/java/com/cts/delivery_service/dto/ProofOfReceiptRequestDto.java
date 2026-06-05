package com.cts.delivery_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProofOfReceiptRequestDto {

    private Integer deliveryId;
    private Integer departmentId;
    private String fileUri;
    private String status;
}
