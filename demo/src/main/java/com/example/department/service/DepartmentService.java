package com.example.department.service;

import java.util.List;

import com.example.department.dto.DepartmentDTO;

public interface DepartmentService {

    void createDepartment(String role, DepartmentDTO dto);

    DepartmentDTO getDepartmentById(String role, Integer departmentId);
    List<DepartmentDTO> getAllDepartments(String role);
}