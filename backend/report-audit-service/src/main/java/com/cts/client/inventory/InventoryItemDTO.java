package com.cts.client.inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryItemDTO {

    private Long itemId;
    private WarehouseRef warehouse;   
    private Long productId;
    private Integer quantity;
    private String receivedAt;
    private String status;

    public Long getWarehouseId() {
        return warehouse != null ? warehouse.getWarehouseId() : null;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WarehouseRef {
        private Long warehouseId;
        private String name;
        private String location;
    }
}