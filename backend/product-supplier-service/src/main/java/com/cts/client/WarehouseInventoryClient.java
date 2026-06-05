package com.cts.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cts.dto.RestockCallDto;

@FeignClient(
    name = "warehouse-inventory-service",
    fallback = WarehouseInventoryClientFallback.class
)
public interface WarehouseInventoryClient {

    @PostMapping("/api/inventory/restock")
    Object restock(@RequestBody RestockCallDto dto);
}
