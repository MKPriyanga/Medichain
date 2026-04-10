package com.example.department.controller;

import com.example.department.service.DepartmentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/department-requests")
public class DepartmentRequestController {

    @Autowired
    private DepartmentRequestService service;

    @PostMapping
    public ResponseEntity<String> createRequest(
            @RequestHeader("Role") String role,
            @RequestBody Map<String, Object> request) {

        service.createRequest(role, request);
        return ResponseEntity.ok("Department request created successfully");
    }

    @PutMapping("/{requestId}/approve")
    public ResponseEntity<String> approve(
            @RequestHeader("Role") String role,
            @PathVariable Integer requestId) {

        service.approveRequest(role, requestId);
        return ResponseEntity.ok("Request approved");
    }

    @PutMapping("/{requestId}/reject")
    public ResponseEntity<String> reject(
            @RequestHeader("Role") String role,
            @PathVariable Integer requestId) {

        service.rejectRequest(role, requestId);
        return ResponseEntity.ok("Request rejected");
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> view(
            @RequestHeader("Role") String role,
            @PathVariable Integer requestId) {

        return ResponseEntity.ok(service.viewRequest(role, requestId));
    }
}
