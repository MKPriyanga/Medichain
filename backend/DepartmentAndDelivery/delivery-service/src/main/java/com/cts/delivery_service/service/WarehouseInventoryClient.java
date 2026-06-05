package com.cts.delivery_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cts.delivery_service.dto.DispatchRequestDto;

@FeignClient(
    name = "warehouse-inventory-service",
    fallback = WarehouseInventoryClientFallback.class
)
public interface WarehouseInventoryClient {

    @PostMapping("/api/inventory/dispatch")
    Object dispatchInventory(@RequestBody DispatchRequestDto dto);
}
