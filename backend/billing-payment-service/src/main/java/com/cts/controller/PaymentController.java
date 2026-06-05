package com.cts.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.entity.Payment;
import com.cts.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // RECORD PAYMENT — ADMIN or PROCUREMENT only
    @PostMapping
    public ResponseEntity<?> recordPayment(
            @Valid @RequestBody Payment payment,
            @RequestHeader("X-Auth-Role") String role) {

        if (!"ADMIN".equals(role) && !"PROCUREMENT".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Only ADMIN or PROCUREMENT can record payments.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.processPayment(payment));
    }

    // GET ALL — ADMIN, PROCUREMENT, AUDITOR
    @GetMapping
    public ResponseEntity<?> getAllPayments(
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {
        if (role != null && !"ADMIN".equals(role) && !"PROCUREMENT".equals(role) && !"AUDITOR".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
}