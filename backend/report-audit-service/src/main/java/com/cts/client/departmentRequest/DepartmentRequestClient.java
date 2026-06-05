package com.cts.client.departmentRequest;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "departmentrequest-service",
    fallback = DepartmentRequestFallBack.class
)
public interface DepartmentRequestClient {

    @GetMapping("/api/department-requests/{requestId}")
    DepartmentRequestDTO getRequestById(
        @PathVariable("requestId") Integer requestId
    );

    @GetMapping("/api/department-requests")
    List<DepartmentRequestDTO> getAllRequests();
}