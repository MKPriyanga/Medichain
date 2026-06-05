package com.cts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KPIDTO {
	
    private Long kpiID;
    private String name;
    private String definition;
    private String target;
    private String currentValue;
    private String reportingPeriod;
    private String category;

}