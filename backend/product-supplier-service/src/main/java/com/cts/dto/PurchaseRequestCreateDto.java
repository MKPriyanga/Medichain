package com.cts.dto;

import lombok.Data;

@Data
public class PurchaseRequestCreateDto {
    private Long productId;
    private Integer quantity;
    private Long departmentRequestId;
    private Long warehouseId;
    private String notes;
}
