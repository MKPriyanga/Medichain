package com.cts.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "scope", nullable = false)
    private String scope;

    @Column(name = "parametersjson", columnDefinition = "TEXT")
    private String parametersJSON;

    @Column(name = "metricsjson", columnDefinition = "TEXT")
    private String metricsJSON;

    @Column(name = "generated_by", nullable = false)
    private String generatedBy;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "report_uri", length = 512)
    private String reportUri;

    @Column(name = "file_name")
    private String fileName;

}