package com.cts.client.inventory;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class InventoryIntegrationService {

    private final InventoryClient inventoryClient;

    public InventoryIntegrationService(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    public List<InventoryItemDTO> getAllInventory() {
        return inventoryClient.getAllInventory();
    }

    public List<InventoryItemDTO> getStockByWarehouse(Long warehouseId) {
        return inventoryClient.getStockByWarehouse(warehouseId);
    }
}