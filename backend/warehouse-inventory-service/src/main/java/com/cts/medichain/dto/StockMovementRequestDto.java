package com.cts.medichain.dto;

import lombok.Data;

@Data
public class StockMovementRequestDto {
    private Long itemId;
    private Long fromWarehouseId;
    private Long toWarehouseId;
    private Integer quantity;
}
