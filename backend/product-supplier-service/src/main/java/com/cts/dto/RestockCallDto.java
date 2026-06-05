package com.cts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestockCallDto {
    private Long warehouseId;
    private Long productId;
    private Integer quantity;
    private Long sourceOrderId;
}
