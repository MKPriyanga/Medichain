package com.example.department.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.department.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
	boolean existsByNameIgnoreCase(String name);
}