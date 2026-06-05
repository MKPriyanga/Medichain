package com.cts.departmentrequest_service.service;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import com.cts.departmentrequest_service.dto.ProductDto;

@FeignClient(
    name = "product-supplier-service",
    fallback = ProductClientFallback.class
)
public interface ProductClient {

    @GetMapping("/api/products")
    List<ProductDto> getAllProducts();
}