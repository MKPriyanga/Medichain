package com.cts.feign;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "warehouse-inventory-service",
    fallback = WarehouseInventoryClient.WarehouseInventoryFallback.class
)
public interface WarehouseInventoryClient {

    @GetMapping("/api/inventory/check")
    Map<String, Object> checkAvailability(
            @RequestParam Long productId,
            @RequestParam Long warehouseId);

    @Component
    class WarehouseInventoryFallback implements WarehouseInventoryClient {
        private static final Logger log =
            LoggerFactory.getLogger(WarehouseInventoryFallback.class);

        @Override
        public Map<String, Object> checkAvailability(Long productId, Long warehouseId) {
            log.warn("warehouse-inventory-service unavailable — inventory check skipped " +
                     "for productId={}", productId);
            return null; // InvoiceService treats null as "check unavailable — proceed with warning"
        }
    }
}