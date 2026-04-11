package com.example.department.controller;

import com.example.department.dto.DepartmentDTO;
import com.example.department.service.DepartmentService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService service;

    @PostMapping
    public ResponseEntity<String> createDepartment(
            @RequestHeader("Role") String role,
            @RequestBody DepartmentDTO dto) {

        service.createDepartment(role, dto);
        return ResponseEntity.ok("Department created successfully");
    }
    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments(
            @RequestHeader("Role") String role) {

        return ResponseEntity.ok(
                service.getAllDepartments(role));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getDepartment(
            @RequestHeader("Role") String role,
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                service.getDepartmentById(role, id));
    }
}