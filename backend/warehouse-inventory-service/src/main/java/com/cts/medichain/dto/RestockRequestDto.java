package com.cts.medichain.dto;

import lombok.Data;

@Data
public class RestockRequestDto {
    private Long warehouseId;
    private Long productId;
    private Integer quantity;
    private Long sourceOrderId; // optional — links restock back to the supplier order
}
