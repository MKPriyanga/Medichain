package com.cts.client.inventory;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "warehouse-inventory-service",
    fallback = InventoryClientFallback.class
)
public interface InventoryClient {

    @GetMapping("/api/inventory/all")
    List<InventoryItemDTO> getAllInventory();

    @GetMapping("/api/inventory/list")
    List<InventoryItemDTO> getStockByWarehouse(@RequestParam("warehouseId") Long warehouseId);
}