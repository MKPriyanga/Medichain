package com.cts.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private Long reportId;
    private String scope;
    private String parametersJSON;
    private String metricsJSON;
    private String generatedBy;
    private LocalDateTime generatedAt;
    private String reportUri;
    private String fileName;

}