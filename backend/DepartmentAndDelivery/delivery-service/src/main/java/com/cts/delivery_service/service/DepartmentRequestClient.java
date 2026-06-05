package com.cts.delivery_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.cts.delivery_service.dto.DepartmentRequestDto;

@FeignClient(
    name = "departmentrequest-service",
    fallback = DepartmentRequestClientFallback.class
)
public interface DepartmentRequestClient {

    @GetMapping("/api/department-requests/{requestId}")
    DepartmentRequestDto getRequest(@PathVariable Integer requestId);
}
