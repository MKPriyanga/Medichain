package com.cts.client.supplier;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
    name = "product-supplier-service",
    fallback = SupplierOrderClientFallback.class
)
public interface SupplierOrderClient {

    @GetMapping("/api/orders")
    List<SupplierOrderDTO> getAllOrders();

    @GetMapping("/api/suppliers")
    List<SupplierDTO> getAllSuppliers();
}