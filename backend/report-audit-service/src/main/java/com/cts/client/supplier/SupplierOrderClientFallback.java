package com.cts.client.supplier;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SupplierOrderClientFallback implements SupplierOrderClient {

    private static final Logger log =
        LoggerFactory.getLogger(SupplierOrderClientFallback.class);

    @Override
    public List<SupplierOrderDTO> getAllOrders() {
        log.warn("product-supplier-service unavailable — returning empty supplier orders for report");
        return Collections.emptyList();
    }

    @Override
    public List<SupplierDTO> getAllSuppliers() {
        log.warn("product-supplier-service unavailable — returning empty suppliers for report");
        return Collections.emptyList();
    }
}