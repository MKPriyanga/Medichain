package com.cts.departmentrequest_service.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.departmentrequest_service.dto.DepartmentRequestCreateDto;
import com.cts.departmentrequest_service.dto.DepartmentRequestResponseDto;
import com.cts.departmentrequest_service.service.DepartmentRequestService;

@RestController
@RequestMapping("/api/department-requests")
public class DepartmenRequestController {

    private final DepartmentRequestService service;

    public DepartmenRequestController(DepartmentRequestService service) {
        this.service = service;
    }

    // CREATE REQUEST — DOCTOR, NURSE only
    @PostMapping
    public ResponseEntity<?> createRequest(
            @RequestBody DepartmentRequestCreateDto dto,
            @RequestHeader("X-Auth-Role") String role,
            @RequestHeader("X-Auth-User") String createdBy,
            @RequestHeader("X-Auth-UserId") String createdByUserId) {

        if (!"DOCTOR".equals(role) && !"NURSE".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Only DOCTOR or NURSE can create requests.");
        }

        DepartmentRequestResponseDto created = service.createRequest(
                dto, createdBy, Long.parseLong(createdByUserId));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // APPROVE REQUEST — ADMIN, DEPARTMENT_HEAD only
    @PutMapping("/{requestId}/approve")
    public ResponseEntity<?> approveRequest(
            @PathVariable Integer requestId,
            @RequestHeader("X-Auth-Role") String role,
            @RequestHeader("X-Auth-User") String username) {

        if (!"ADMIN".equals(role) && !"DEPARTMENT_HEAD".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Only ADMIN or DEPARTMENT_HEAD can approve requests.");
        }

        DepartmentRequestResponseDto updated = service.approve(requestId, username);
        return ResponseEntity.ok(updated);
    }

    // REJECT REQUEST — ADMIN, DEPARTMENT_HEAD only
    @PutMapping("/{requestId}/reject")
    public ResponseEntity<?> rejectRequest(
            @PathVariable Integer requestId,
            @RequestHeader("X-Auth-Role") String role,
            @RequestHeader("X-Auth-User") String username) {

        if (!"ADMIN".equals(role) && !"DEPARTMENT_HEAD".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Only ADMIN or DEPARTMENT_HEAD can reject requests.");
        }

        DepartmentRequestResponseDto updated = service.reject(requestId, username);
        return ResponseEntity.ok(updated);
    }

    // MARK PROCESSING — WAREHOUSE only
    @PutMapping("/{requestId}/process")
    public ResponseEntity<?> markProcessing(
            @PathVariable Integer requestId,
            @RequestHeader("X-Auth-Role") String role,
            @RequestHeader("X-Auth-User") String username) {

        if (!"WAREHOUSE".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Only WAREHOUSE can mark requests as PROCESSING.");
        }

        try {
            return ResponseEntity.ok(service.markProcessing(requestId, username));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // MARK COMPLETED — WAREHOUSE only
    @PutMapping("/{requestId}/complete")
    public ResponseEntity<?> markCompleted(
            @PathVariable Integer requestId,
            @RequestHeader("X-Auth-Role") String role,
            @RequestHeader("X-Auth-User") String username) {

        if (!"WAREHOUSE".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Only WAREHOUSE can mark requests as COMPLETED.");
        }

        try {
            return ResponseEntity.ok(service.markCompleted(requestId, username));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // VIEW REQUEST — ADMIN, DEPARTMENT_HEAD, DOCTOR, NURSE
    // required=false so internal Feign calls (report-audit-service) also work
    @GetMapping("/{requestId}")
    public ResponseEntity<?> viewRequest(
            @PathVariable Integer requestId,
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

        if (role != null && !"ADMIN".equals(role)
                && !"DEPARTMENT_HEAD".equals(role)
                && !"DOCTOR".equals(role)
                && !"NURSE".equals(role)
                && !"WAREHOUSE".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied.");
        }

        return ResponseEntity.ok(service.view(requestId));
    }

    // GET ALL — ADMIN, DEPARTMENT_HEAD, WAREHOUSE
    // DOCTOR / NURSE can also call this; frontend filters to show only their own requests
    // required=false so internal Feign calls (report-audit-service, kpi-service) also work
    @GetMapping
    public ResponseEntity<?> getAllRequests(
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

        // Allow internal service-to-service calls (no role header) through
        if (role != null && !"ADMIN".equals(role)
                && !"DEPARTMENT_HEAD".equals(role)
                && !"WAREHOUSE".equals(role)
                && !"DOCTOR".equals(role)
                && !"NURSE".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied.");
        }

        return ResponseEntity.ok(service.getAllRequests());
    }

}