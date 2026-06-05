package com.cts.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "departmentrequest-service",
    fallback = DepartmentRequestValidationFallback.class
)
public interface DepartmentRequestValidationClient {

    @GetMapping("/api/department-requests/{requestId}")
    DepartmentRequestDTO getRequestById(@PathVariable("requestId") Long requestId);
}