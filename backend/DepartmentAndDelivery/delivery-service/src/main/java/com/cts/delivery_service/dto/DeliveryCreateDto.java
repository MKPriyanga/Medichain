package com.cts.delivery_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryCreateDto {

    private Integer requestId;
    private Integer quantity;
    private Long warehouseId;
}
