package com.cts.controller;

import com.cts.dto.ApprovePurchaseRequestDto;
import com.cts.dto.PurchaseRequestCreateDto;
import com.cts.entity.PurchaseRequest;
import com.cts.entity.SupplierOrder;
import com.cts.service.PurchaseRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-requests")
public class PurchaseRequestController {

    private final PurchaseRequestService purchaseRequestService;

    public PurchaseRequestController(PurchaseRequestService purchaseRequestService) {
        this.purchaseRequestService = purchaseRequestService;
    }

    // ── CREATE — WAREHOUSE only ───────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> createPurchaseRequest(
            @RequestBody PurchaseRequestCreateDto dto,
            @RequestHeader("X-Auth-Role") String role,
            @RequestHeader("X-Auth-UserId") String userId) {

        if (!"WAREHOUSE".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Only WAREHOUSE operators can raise purchase requests.");
        }

        PurchaseRequest created = purchaseRequestService.create(dto, Long.parseLong(userId));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ── GET ALL ───────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<?> getAllPurchaseRequests(
            @RequestHeader("X-Auth-Role") String role,
            @RequestHeader("X-Auth-UserId") String userId) {

        if (!"WAREHOUSE".equals(role) && !"PROCUREMENT".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }

        List<PurchaseRequest> result = purchaseRequestService.getAll(role, Long.parseLong(userId));
        return ResponseEntity.ok(result);
    }

    // ── GET BY ID ──────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getPurchaseRequestById(
            @PathVariable Long id,
            @RequestHeader("X-Auth-Role") String role) {

        if (!"WAREHOUSE".equals(role) && !"PROCUREMENT".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }
        return ResponseEntity.ok(purchaseRequestService.getById(id));
    }

    // ── APPROVE — PROCUREMENT / ADMIN ──────────────────────────────────────────
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approvePurchaseRequest(
            @PathVariable Long id,
            @RequestBody ApprovePurchaseRequestDto dto,
            @RequestHeader("X-Auth-Role") String role,
            @RequestHeader("X-Auth-UserId") String userId) {

        if (!"PROCUREMENT".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Only PROCUREMENT or ADMIN can approve purchase requests.");
        }

        SupplierOrder order = purchaseRequestService.approve(id, dto.getSupplierId(), Long.parseLong(userId));
        return ResponseEntity.ok(order);
    }

    // ── REJECT — PROCUREMENT / ADMIN ───────────────────────────────────────────
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectPurchaseRequest(
            @PathVariable Long id,
            @RequestHeader("X-Auth-Role") String role,
            @RequestHeader("X-Auth-UserId") String userId) {

        if (!"PROCUREMENT".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Only PROCUREMENT or ADMIN can reject purchase requests.");
        }

        PurchaseRequest rejected = purchaseRequestService.reject(id, Long.parseLong(userId));
        return ResponseEntity.ok(rejected);
    }
}
