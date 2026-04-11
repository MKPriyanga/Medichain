package com.example.department.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.department.entity.DepartmentRequest;

public interface DepartmentRequestRepository
        extends JpaRepository<DepartmentRequest, Integer> {

    boolean existsByDepartmentIdAndStatus(Integer departmentId, String status);
}