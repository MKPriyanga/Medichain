package com.cts.delivery_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispatchRequestDto {
    private Long warehouseId;
    private Long productId;
    private Integer quantity;
}
