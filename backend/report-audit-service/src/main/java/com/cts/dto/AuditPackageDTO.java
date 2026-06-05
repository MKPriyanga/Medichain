package com.cts.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditPackageDTO {

    private Long          packageId;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private String        contentsJSON;
    private LocalDateTime generatedAt;
    private String        packageUri;
}