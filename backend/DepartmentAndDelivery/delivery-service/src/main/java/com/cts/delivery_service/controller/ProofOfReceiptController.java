package com.cts.delivery_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.delivery_service.dto.ProofOfReceiptRequestDto;
import com.cts.delivery_service.dto.ProofOfReceiptResponseDto;
import com.cts.delivery_service.service.ProofOfReceiptService;

@RestController
@RequestMapping("/api/proof-of-receipt")
public class ProofOfReceiptController {

    private final ProofOfReceiptService service;

    public ProofOfReceiptController(ProofOfReceiptService service) {
        this.service = service;
    }

    // CREATE PROOF — WAREHOUSE only
    @PostMapping
    public ResponseEntity<?> createProof(
            @RequestBody ProofOfReceiptRequestDto dto,
            @RequestHeader("X-Auth-Role") String role) {

        if (!"WAREHOUSE".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Only WAREHOUSE can upload proof of receipt.");
        }

        ProofOfReceiptResponseDto created = service.createProof(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET BY DELIVERY ID — ADMIN, WAREHOUSE, AUDITOR, DEPARTMENT_HEAD
    @GetMapping("/delivery/{deliveryId}")
    public ResponseEntity<?> getByDelivery(
            @PathVariable Integer deliveryId,
            @RequestHeader("X-Auth-Role") String role) {

        if (!"ADMIN".equals(role) && !"WAREHOUSE".equals(role)
                && !"AUDITOR".equals(role) && !"DEPARTMENT_HEAD".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied.");
        }

        return ResponseEntity.ok(service.getByDeliveryId(deliveryId));
    }

    // GET BY DEPARTMENT ID — ADMIN, WAREHOUSE, AUDITOR, DEPARTMENT_HEAD
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<?> getByDepartment(
            @PathVariable Integer departmentId,
            @RequestHeader("X-Auth-Role") String role) {

        if (!"ADMIN".equals(role) && !"WAREHOUSE".equals(role)
                && !"AUDITOR".equals(role) && !"DEPARTMENT_HEAD".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied.");
        }

        return ResponseEntity.ok(service.getByDepartmentId(departmentId));
    }

    // GET BY PROOF ID — ADMIN, WAREHOUSE, AUDITOR, DEPARTMENT_HEAD
    @GetMapping("/{proofId}")
    public ResponseEntity<?> getById(
            @PathVariable Integer proofId,
            @RequestHeader("X-Auth-Role") String role) {

        if (!"ADMIN".equals(role) && !"WAREHOUSE".equals(role)
                && !"AUDITOR".equals(role) && !"DEPARTMENT_HEAD".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied.");
        }

        return ResponseEntity.ok(service.getById(proofId));
    }
}
