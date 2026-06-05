package com.cts.department_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.department_service.dto.DepartmentRequestDto;
import com.cts.department_service.dto.DepartmentResponseDto;
import com.cts.department_service.service.DepartmentService;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService service;

    public DepartmentController(DepartmentService service) {
        this.service = service;
    }

    // CREATE DEPARTMENT — ADMIN only
    @PostMapping
    public ResponseEntity<?> createDepartment(
            @RequestBody DepartmentRequestDto dto,
            @RequestHeader("X-Auth-Role") String role) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Only ADMIN can create departments.");
        }

        DepartmentResponseDto created = service.createDepartment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // UPDATE DEPARTMENT — ADMIN only
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(
            @PathVariable Integer id,
            @RequestBody DepartmentRequestDto dto,
            @RequestHeader("X-Auth-Role") String role) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Only ADMIN can update departments.");
        }

        DepartmentResponseDto updated = service.updateDepartment(id, dto);
        return ResponseEntity.ok(updated);
    }

    // GET ALL — ALL roles
    @GetMapping
    public ResponseEntity<List<DepartmentResponseDto>> getAllDepartments() {
        return ResponseEntity.ok(service.getAllDepartments());
    }

    // GET BY ID — ALL roles
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponseDto> getDepartment(
            @PathVariable Integer id) {
        return ResponseEntity.ok(service.getDepartmentById(id));
    }
}