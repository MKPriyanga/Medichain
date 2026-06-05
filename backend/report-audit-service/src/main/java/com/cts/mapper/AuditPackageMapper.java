package com.cts.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.cts.dto.AuditPackageDTO;
import com.cts.entity.AuditPackage;

@Component
public class AuditPackageMapper {

    public AuditPackageDTO toDTO(AuditPackage ap) {
        if (ap == null) return null;

        AuditPackageDTO dto = new AuditPackageDTO();
        dto.setPackageId(ap.getPackageId());
        dto.setPeriodStart(ap.getPeriodStart());
        dto.setPeriodEnd(ap.getPeriodEnd());
        dto.setGeneratedAt(ap.getGeneratedAt());
        dto.setContentsJSON(ap.getContentsJSON());
        return dto;
    }

    public AuditPackage toEntity(AuditPackageDTO dto) {
        if (dto == null) return null;

        AuditPackage ap = new AuditPackage();
        ap.setPackageId(dto.getPackageId());
        ap.setPeriodStart(dto.getPeriodStart());
        ap.setPeriodEnd(dto.getPeriodEnd());
        ap.setContentsJSON(dto.getContentsJSON());
        ap.setGeneratedAt(
            dto.getGeneratedAt() != null
                ? dto.getGeneratedAt()
                : LocalDateTime.now());
        return ap;
    }
}