package com.cts.delivery_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cts.delivery_service.dto.DispatchRequestDto;

@Component
public class WarehouseInventoryClientFallback implements WarehouseInventoryClient {

    private static final Logger log = LoggerFactory.getLogger(WarehouseInventoryClientFallback.class);

    @Override
    public Object dispatchInventory(DispatchRequestDto dto) {
        log.warn("WarehouseInventoryClient fallback: could not dispatch productId={}, warehouseId={}, qty={}",
                dto.getProductId(), dto.getWarehouseId(), dto.getQuantity());
        return null;
    }
}
