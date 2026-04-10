package com.example.department.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.department.entity.DepartmentRequest;

@Repository
public interface DepartmentRequestRepository
        extends JpaRepository<DepartmentRequest, Integer> {

    boolean existsByDepartmentIdAndStatus(Integer departmentId, String status);
}
