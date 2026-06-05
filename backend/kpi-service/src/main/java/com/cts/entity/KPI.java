package com.cts.entity;

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
@Table(name = "kpi")
public class KPI {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="kpi_id")
    private Long kpiID;

    private String name;
    private String definition;
    private String target;
    private String currentValue;
    private String reportingPeriod;
    private String category;

}