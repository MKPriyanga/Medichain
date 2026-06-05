package com.cts.department_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.department_service.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Integer>  {

	boolean existsByNameIgnoreCase(String name);
	boolean existsByHeadId(Integer headId); 
    boolean existsByHeadIdAndDepartmentIdNot(Integer headId, Integer departmentId);
}
