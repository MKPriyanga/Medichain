package com.cts.client.inventory;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class InventoryClientFallback implements InventoryClient {

    private static final Logger log = LoggerFactory.getLogger(InventoryClientFallback.class);

    @Override
    public List<InventoryItemDTO> getAllInventory() {
        log.warn("warehouse-inventory-service unavailable — returning empty inventory list");
        return Collections.emptyList();
    }

    @Override
    public List<InventoryItemDTO> getStockByWarehouse(Long warehouseId) {
        log.warn("warehouse-inventory-service unavailable — returning empty list for warehouseId={}", warehouseId);
        return Collections.emptyList();
    }

}