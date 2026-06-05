package com.cts.feign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cts.feign.DepartmentRequestDTO;
import com.cts.feign.DepartmentRequestValidationClient;

@Component
public class DepartmentRequestValidationFallback implements DepartmentRequestValidationClient {

    private static final Logger log = LoggerFactory.getLogger(DepartmentRequestValidationFallback.class);

    @Override
    public DepartmentRequestDTO getRequestById(Long requestId) {
        log.warn("departmentrequest-service unavailable — cannot validate requestId={}", requestId);
        return null;  // InvoiceService checks for null and throws InvalidRequestException
    }
}