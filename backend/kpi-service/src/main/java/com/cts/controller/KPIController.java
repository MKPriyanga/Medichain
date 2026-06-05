package com.cts.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.dto.KPIDTO;
import com.cts.response.ApiResponse;
import com.cts.service.KPIService;

@RestController
@RequestMapping("/api/kpi")
public class KPIController {

    @Autowired
    private KPIService kpiService;

    // GET ALL KPIs — ADMIN, AUDITOR, DEPARTMENT_HEAD, PROCUREMENT
        @GetMapping
    public ResponseEntity<?> getAllKPIs(
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

        if (role != null
                && !"ADMIN".equals(role)
                && !"AUDITOR".equals(role)
                && !"DEPARTMENT_HEAD".equals(role)
                && !"PROCUREMENT".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Allowed: ADMIN, AUDITOR, DEPARTMENT_HEAD, PROCUREMENT");
        }

        List<KPIDTO> kpis = kpiService.getAllKPIs();
        return ResponseEntity.ok(ApiResponse.success("KPI metrics retrieved successfully", kpis));
    }

    // FILTER BY CATEGORY — ADMIN, AUDITOR, DEPARTMENT_HEAD
    @GetMapping("/filter")
    public ResponseEntity<?> getKPIsByCategory(
            @RequestParam String category,
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

        if (role != null
                && !"ADMIN".equals(role)
                && !"AUDITOR".equals(role)
                && !"DEPARTMENT_HEAD".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Allowed: ADMIN, AUDITOR, DEPARTMENT_HEAD");
        }

        List<KPIDTO> filteredKpis = kpiService.getKPIsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success("KPIs filtered by category: " + category, filteredKpis));
    }


    // SYNC KPIs — ADMIN only
    @PostMapping("/sync")
    public ResponseEntity<?> syncKpis(
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

    	if (!"ADMIN".equals(role)) {
    	    return ResponseEntity.status(HttpStatus.FORBIDDEN)
    	            .body(ApiResponse.error("Access denied. Only ADMIN can sync KPIs."));
    	}

        kpiService.syncRemoteKpis();
        return ResponseEntity.ok(ApiResponse.success(
            "KPI sync complete — request fulfillment, delivery completion, and stock utilization updated", "OK"));
    }
}