package com.cts.departmentrequest_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cts.departmentrequest_service.entity.DepartmentRequest;

public interface DepartmentRequestRepository
        extends JpaRepository<DepartmentRequest, Integer> {

    boolean existsByDepartmentIdAndStatus(Integer departmentId, String status);
}
