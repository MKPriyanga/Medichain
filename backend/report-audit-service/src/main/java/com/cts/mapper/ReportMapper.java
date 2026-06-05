package com.cts.mapper;

import java.time.LocalDateTime;
import com.cts.dto.ReportDTO;
import com.cts.entity.Report;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public ReportDTO toDTO(Report report) {
        if (report == null) return null;

        ReportDTO dto = new ReportDTO();
        dto.setReportId(report.getReportId());
        dto.setScope(report.getScope());
        dto.setParametersJSON(report.getParametersJSON());
        dto.setMetricsJSON(report.getMetricsJSON());
        dto.setGeneratedBy(report.getGeneratedBy());
        dto.setGeneratedAt(report.getGeneratedAt());
        dto.setReportUri(report.getReportUri());
        dto.setFileName(report.getFileName());
        return dto;
    }

    public Report toEntity(ReportDTO dto) {
        if (dto == null) return null;

        Report report = new Report();
        report.setScope(dto.getScope());
        report.setParametersJSON(dto.getParametersJSON());
        report.setMetricsJSON(dto.getMetricsJSON());
        report.setGeneratedBy(
            dto.getGeneratedBy() != null ? dto.getGeneratedBy() : "SYSTEM");
        report.setGeneratedAt(
            dto.getGeneratedAt() != null ? dto.getGeneratedAt() : LocalDateTime.now());
        report.setReportUri(dto.getReportUri());
        report.setFileName(dto.getFileName());
        return report;
    }
}