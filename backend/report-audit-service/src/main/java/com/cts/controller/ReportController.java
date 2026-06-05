package com.cts.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.dto.ReportDTO;
import com.cts.response.ApiResponse;
import com.cts.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // GENERATE REPORT — ADMIN, AUDITOR, PROCUREMENT
    @PostMapping("/generate")
    public ResponseEntity<?> generate(
            @RequestParam String scope,
            @RequestParam(defaultValue = "ALL") String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer id,
            @RequestHeader(value = "X-Auth-User", defaultValue = "SYSTEM") String generatedBy,
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

        if (role != null
                && !"ADMIN".equals(role)
                && !"AUDITOR".equals(role)
                && !"PROCUREMENT".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Allowed: ADMIN, AUDITOR, PROCUREMENT");
        }

        ReportDTO result = reportService.generateReport(scope, type, status, id, generatedBy);
        return ResponseEntity.ok(
            ApiResponse.success(
                "Report generated | scope: " + scope.toUpperCase()
                + " | type: " + type.toUpperCase()
                + " | by: " + generatedBy,
                result)
        );
    }

    // DOWNLOAD REPORT — ADMIN, AUDITOR
    // Serves metricsJSON directly from DB as a downloadable JSON file
    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadReport(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

        if (role != null
                && !"ADMIN".equals(role)
                && !"AUDITOR".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Allowed: ADMIN, AUDITOR");
        }

        ReportDTO report = reportService.getReportDTOById(id);

        if (report.getMetricsJSON() == null || report.getMetricsJSON().isBlank()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No content stored for report #" + id);
        }

        byte[] bytes = report.getMetricsJSON()
            .getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String fileName = report.getFileName() != null
            ? report.getFileName() : "report_" + id + ".json";

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment().filename(fileName).build().toString())
            .body(new ByteArrayResource(bytes));
    }

    // CREATE REPORT — ADMIN only
    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestBody ReportDTO reportDTO,
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Only ADMIN can create reports.");
        }

        ReportDTO created = reportService.createReport(reportDTO);
        return new ResponseEntity<>(
            ApiResponse.success("Report record saved", created), HttpStatus.CREATED
        );
    }

    // LIST ALL REPORTS — ADMIN, AUDITOR
    @GetMapping
    public ResponseEntity<?> listReports(
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

        if (role != null
                && !"ADMIN".equals(role)
                && !"AUDITOR".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Allowed: ADMIN, AUDITOR");
        }

        return ResponseEntity.ok(
            ApiResponse.success("All reports retrieved", reportService.getAllReportsDTO())
        );
    }

    // GET REPORT BY ID — ADMIN, AUDITOR
    @GetMapping("/{id}")
    public ResponseEntity<?> getReportById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

        if (role != null
                && !"ADMIN".equals(role)
                && !"AUDITOR".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Allowed: ADMIN, AUDITOR");
        }

        return ResponseEntity.ok(
            ApiResponse.success("Report found", reportService.getReportDTOById(id))
        );
    }

    // DELETE REPORT — ADMIN only
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Only ADMIN can delete reports.");
        }

        reportService.deleteReportById(id);
        return ResponseEntity.ok(ApiResponse.success("Success", "Report " + id + " deleted."));
    }
}