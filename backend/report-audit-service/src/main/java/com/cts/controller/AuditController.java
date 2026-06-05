package com.cts.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.dto.AuditPackageDTO;
import com.cts.response.ApiResponse;
import com.cts.service.AuditPackageService;


@RestController
@RequestMapping("/api/audit-packages")
public class AuditController {

    @Autowired
    private AuditPackageService auditService;

    // GENERATE — ADMIN, AUDITOR
    @PostMapping("/generate")
    public ResponseEntity<?> generate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "ALL") String scope,
            @RequestHeader(value = "X-Auth-User", defaultValue = "SYSTEM") String generatedBy,
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

        if (role != null && !"ADMIN".equals(role) && !"AUDITOR".equals(role))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Allowed: ADMIN, AUDITOR");

        AuditPackageDTO generated = auditService.generatePackage(start, end, scope, generatedBy);
        return new ResponseEntity<>(
            ApiResponse.success(
                "Audit package generated | scope: " + scope.toUpperCase()
                + " | period: " + start + " to " + end
                + " | by: " + generatedBy,
                generated),
            HttpStatus.CREATED);
    }

    // CREATE MANUALLY — ADMIN only
    @PostMapping
    public ResponseEntity<?> createPackage(
            @RequestBody AuditPackageDTO dto,
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

        if (!"ADMIN".equals(role))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Only ADMIN can create audit packages manually.");

        AuditPackageDTO created = auditService.createPackage(dto);
        return new ResponseEntity<>(
            ApiResponse.success("Audit package created manually", created), HttpStatus.CREATED);
    }

    // LIST ALL — ADMIN, AUDITOR
    @GetMapping
    public ResponseEntity<?> listAll(
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

        if (role != null && !"ADMIN".equals(role) && !"AUDITOR".equals(role))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Allowed: ADMIN, AUDITOR");

        return ResponseEntity.ok(
            ApiResponse.success("All audit packages retrieved", auditService.getAllPackages()));
    }

    // GET BY ID — ADMIN, AUDITOR
    @GetMapping("/{id}")
    public ResponseEntity<?> getDetail(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

        if (role != null && !"ADMIN".equals(role) && !"AUDITOR".equals(role))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Allowed: ADMIN, AUDITOR");

        return ResponseEntity.ok(
            ApiResponse.success("Audit package found", auditService.getPackageById(id)));
    }

    // SEARCH BY PERIOD — ADMIN, AUDITOR
    @GetMapping("/search")
    public ResponseEntity<?> searchByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

        if (role != null && !"ADMIN".equals(role) && !"AUDITOR".equals(role))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Allowed: ADMIN, AUDITOR");

        return ResponseEntity.ok(
            ApiResponse.success("Audit packages found for period",
                auditService.getPackagesByPeriod(start, end)));
    }

    // UPDATE — ADMIN only
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePackage(
            @PathVariable Long id,
            @RequestBody AuditPackageDTO dto,
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

        if (!"ADMIN".equals(role))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Only ADMIN can update audit packages.");

        return ResponseEntity.ok(
            ApiResponse.success("Audit package updated", auditService.updatePackage(id, dto)));
    }

    // DELETE — ADMIN only
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePackage(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

        if (!"ADMIN".equals(role))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Only ADMIN can delete audit packages.");

        auditService.deletePackage(id);
        return ResponseEntity.ok(
            ApiResponse.success("Success", "Audit Package " + id + " deleted."));
    }

    // DOWNLOAD — ADMIN, AUDITOR
    // Serves contentsJSON directly from DB as a downloadable JSON file
    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadPackage(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

        if (role != null && !"ADMIN".equals(role) && !"AUDITOR".equals(role))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Allowed: ADMIN, AUDITOR");

        AuditPackageDTO pkg = auditService.getPackageById(id);

        if (pkg.getContentsJSON() == null || pkg.getContentsJSON().isBlank())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No content stored for audit package #" + id);

        byte[] bytes    = pkg.getContentsJSON().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String fileName = "audit_package_" + id + ".json";

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment().filename(fileName).build().toString())
            .body(new ByteArrayResource(bytes));
    }
}