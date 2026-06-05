package com.cts.departmentrequest_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cts.departmentrequest_service.dto.DepartmentDto;

@FeignClient(
    name = "department-service",
    fallback = DepartmentClientFallback.class
)
public interface DepartmentClient {

    @GetMapping("/api/departments/{id}")
    DepartmentDto getDepartment(@PathVariable Integer id);
}
         