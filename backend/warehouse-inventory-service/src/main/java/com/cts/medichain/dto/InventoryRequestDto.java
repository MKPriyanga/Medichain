package com.cts.medichain.dto;

import lombok.Data;

@Data
public class InventoryRequestDto {
    private Long warehouseId;
    private Long productId;
    private Integer quantity;
}
