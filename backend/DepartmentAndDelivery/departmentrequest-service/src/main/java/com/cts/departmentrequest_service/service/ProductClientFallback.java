package com.cts.departmentrequest_service.service;

import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.cts.departmentrequest_service.dto.ProductDto;

@Component
public class ProductClientFallback implements ProductClient {

    private static final Logger log = 
        LoggerFactory.getLogger(ProductClientFallback.class);

//    @Override
//    public List<ProductDto> getAllProducts() {
//        log.warn("Product service unavailable — " +
//                 "returning empty list, product validation skipped");
//        return Collections.emptyList();
//    }
    @Override
    public List<ProductDto> getAllProducts() {
        log.warn("Product service unavailable — returning null");
        return null;  // was returning empty list
    }
}