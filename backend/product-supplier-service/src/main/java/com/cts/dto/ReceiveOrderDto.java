package com.cts.dto;

import lombok.Data;

@Data
public class ReceiveOrderDto {
    private Long warehouseId;
    private Integer quantityReceived;
}
