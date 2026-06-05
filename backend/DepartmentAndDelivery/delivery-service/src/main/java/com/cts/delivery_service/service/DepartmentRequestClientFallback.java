package com.cts.delivery_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.cts.delivery_service.dto.DepartmentRequestDto;

@Component
public class DepartmentRequestClientFallback implements DepartmentRequestClient {

    private static final Logger log = 
        LoggerFactory.getLogger(DepartmentRequestClientFallback.class);

    @Override
    public DepartmentRequestDto getRequest(Integer requestId) {
        log.warn("DepartmentRequest service unavailable — " +
                 "returning null for requestId: {}", requestId);
        return null;
    }
}