package com.cts.client.supplier;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierOrderDTO {
    private Long orderId;
    private Long supplierId;
    private String productIdsJson;
    private Integer quantity;
    private LocalDateTime orderedAt;
    private String status;
}