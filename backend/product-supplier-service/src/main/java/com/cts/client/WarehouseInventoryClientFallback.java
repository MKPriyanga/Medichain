package com.cts.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cts.dto.RestockCallDto;

@Component
public class WarehouseInventoryClientFallback implements WarehouseInventoryClient {

    private static final Logger log = LoggerFactory.getLogger(WarehouseInventoryClientFallback.class);

    @Override
    public Object restock(RestockCallDto dto) {
        log.warn("WarehouseInventoryClient fallback: could not restock productId={}, warehouseId={}, qty={}",
                dto.getProductId(), dto.getWarehouseId(), dto.getQuantity());
        return null;
    }
}
