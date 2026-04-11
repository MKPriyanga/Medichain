package com.example.department.service;

import com.example.department.dto.DepartmentDTO;
import com.example.department.entity.Department;
import com.example.department.exception.InvalidRequestException;
import com.example.department.exception.UnauthorizedRoleException;
import com.example.department.repository.DepartmentRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository repository;

    @Override
    public void createDepartment(String role, DepartmentDTO dto) {

        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new UnauthorizedRoleException(
                    "Only Admin can create departments");
        }

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new InvalidRequestException("Department name is required");
        }

        if (repository.existsByNameIgnoreCase(dto.getName().trim())) {
            throw new InvalidRequestException("Department already created");
        }

        Department department = new Department();
        department.setName(dto.getName().trim());

        // ✅ NEW COLUMN SET SAFELY (default behavior)
        department.setStatus("ACTIVE");

        repository.save(department);
    }

    @Override
    public DepartmentDTO getDepartmentById(String role, Integer departmentId) {

        if (!"ADMIN".equalsIgnoreCase(role)
                && !"DEPARTMENT_HEAD".equalsIgnoreCase(role)) {
            throw new UnauthorizedRoleException("Access denied");
        }

        Department department = repository.findById(departmentId)
                .orElseThrow(() ->
                        new InvalidRequestException("Department not found"));

        return new DepartmentDTO(
                department.getDepartmentId(),
                department.getName()
        );
    }
    @Override
    public List<DepartmentDTO> getAllDepartments(String role) {

        if (!"ADMIN".equalsIgnoreCase(role)
                && !"DEPARTMENT_HEAD".equalsIgnoreCase(role)) {
            throw new UnauthorizedRoleException("Access denied");
        }

        return repository.findAll()
                .stream()
                .map(dept -> new DepartmentDTO(
                        dept.getDepartmentId(),
                        dept.getName()))
                .toList();
    }
}