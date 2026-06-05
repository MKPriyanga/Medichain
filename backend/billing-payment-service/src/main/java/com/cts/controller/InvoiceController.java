package com.cts.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.entity.Invoice;
import com.cts.exception.InvalidRequestException;
import com.cts.service.InvoiceService;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    // CREATE — ADMIN or PROCUREMENT only
    @PostMapping
    public ResponseEntity<?> createInvoice(
            @Valid @RequestBody Invoice invoice,
            @RequestHeader("X-Auth-Role") String role) {

        if (!"ADMIN".equals(role) && !"PROCUREMENT".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Only ADMIN or PROCUREMENT can create invoices.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.createInvoice(invoice));
    }

    // GET ALL — ADMIN, PROCUREMENT, AUDITOR
    @GetMapping
    public ResponseEntity<?> getAllInvoices(
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {
        if (role != null && !"ADMIN".equals(role) && !"PROCUREMENT".equals(role) && !"AUDITOR".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    // GET BY ID — ADMIN, PROCUREMENT, AUDITOR
    @GetMapping("/{id}")
    public ResponseEntity<?> getInvoice(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {
        if (role != null && !"ADMIN".equals(role) && !"PROCUREMENT".equals(role) && !"AUDITOR".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }
}