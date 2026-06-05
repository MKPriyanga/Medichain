package com.medichain.iam.controller;
 
import com.medichain.iam.entity.AuditLog;

import com.medichain.iam.service.AuditLogService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController

@RequestMapping("/api/admin/audit-logs")

@RequiredArgsConstructor

public class AuditLogController {
 
    private final AuditLogService auditLogService;
 
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")

    @GetMapping

    public ResponseEntity<List<AuditLog>> getAllLogs(

            @RequestParam(required = false) Long userId,

            @RequestParam(required = false) String resourceType,

            @RequestParam(required = false) String resourceId) {

        return ResponseEntity.ok(auditLogService.getLogs(userId, resourceType, resourceId));

    }

}
 