package com.cts.department_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cts.department_service.dto.*;
import com.cts.department_service.entity.Department;
import com.cts.department_service.exception.InvalidRequestException;
import com.cts.department_service.repository.DepartmentRepository;

@Service
public class DepartmentService {

    private final DepartmentRepository repository;

    public DepartmentService(DepartmentRepository repository) {
        this.repository = repository;
    }

    // CREATE
    public DepartmentResponseDto createDepartment(DepartmentRequestDto dto) {

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new InvalidRequestException("Department name is required");
        }

        if (repository.existsByNameIgnoreCase(dto.getName().trim())) {
            throw new InvalidRequestException("Department already exists");
        }

        // VALIDATE headId is not already assigned to another department
        if (dto.getHeadId() != null && repository.existsByHeadId(dto.getHeadId())) {
            throw new InvalidRequestException(
                    "HeadId " + dto.getHeadId() +
                    " is already assigned to another department");
        }

        Department department = Department.builder()
                .name(dto.getName().trim())
                .headId(dto.getHeadId())
                .contactInfo(dto.getContactInfo())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        Department saved = repository.save(department);
        return mapToResponse(saved);
    }

    // UPDATE DEPARTMENT
    public DepartmentResponseDto updateDepartment(Integer id, DepartmentRequestDto dto) {

        Department department = repository.findById(id)
                .orElseThrow(() ->
                        new InvalidRequestException("Department not found"));

        // VALIDATE new headId is not assigned to another department
        if (dto.getHeadId() != null &&
                repository.existsByHeadIdAndDepartmentIdNot(dto.getHeadId(), id)) {
            throw new InvalidRequestException(
                    "HeadId " + dto.getHeadId() +
                    " is already assigned to another department");
        }

        department.setName(dto.getName() != null ? dto.getName() : department.getName());
        department.setHeadId(dto.getHeadId() != null ? dto.getHeadId() : department.getHeadId());
        department.setContactInfo(dto.getContactInfo() != null ? dto.getContactInfo() : department.getContactInfo());
        department.setStatus(dto.getStatus() != null ? dto.getStatus() : department.getStatus());

        Department saved = repository.save(department);
        return mapToResponse(saved);
    }

    // GET BY ID
    public DepartmentResponseDto getDepartmentById(Integer id) {

        Department dept = repository.findById(id)
                .orElseThrow(() ->
                        new InvalidRequestException("Department not found"));

        return mapToResponse(dept);
    }

    // GET ALL
    public List<DepartmentResponseDto> getAllDepartments() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // MAPPER
    private DepartmentResponseDto mapToResponse(Department dept) {
        return new DepartmentResponseDto(
                dept.getDepartmentId(),
                dept.getName(),
                dept.getHeadId(),
                dept.getContactInfo(),
                dept.getStatus()
        );
    }
}