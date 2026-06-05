package com.cts.departmentrequest_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.cts.departmentrequest_service.dto.DepartmentDto;

@Component
public class DepartmentClientFallback implements DepartmentClient {

    private static final Logger log = 
        LoggerFactory.getLogger(DepartmentClientFallback.class);
//
//    @Override
//    public DepartmentDto getDepartment(Integer id) {
//        log.warn("Department service unavailable — " +
//                 "returning default for departmentId: {}", id);
//        return new DepartmentDto(id, "Default Department", "INACTIVE", 
//                                 "Department Service is DOWN", null);
//    }
    @Override
    public DepartmentDto getDepartment(Integer id) {
        log.warn("Department service unavailable — returning null for departmentId: {}", id);
        return null;
    }
}