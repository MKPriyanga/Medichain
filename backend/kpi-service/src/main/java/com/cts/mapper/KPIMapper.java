package com.cts.mapper;

import org.springframework.stereotype.Component;

import com.cts.dto.KPIDTO;
import com.cts.entity.KPI;

@Component
public class KPIMapper {

    public KPIDTO toDTO(KPI kpi) {
        if (kpi == null) {
            return null;
        }

        KPIDTO dto = new KPIDTO();
        dto.setKpiID(kpi.getKpiID());
        dto.setName(kpi.getName());
        dto.setDefinition(kpi.getDefinition());
        dto.setTarget(kpi.getTarget());
        dto.setCurrentValue(kpi.getCurrentValue());
        dto.setReportingPeriod(kpi.getReportingPeriod());
        dto.setCategory(kpi.getCategory());

        return dto;
    }

    public KPI toEntity(KPIDTO dto) {
        if (dto == null) {
            return null;
        }

        KPI kpi = new KPI();
        kpi.setKpiID(dto.getKpiID()); 
        kpi.setName(dto.getName());
        kpi.setDefinition(dto.getDefinition());
        kpi.setTarget(dto.getTarget());
        kpi.setCurrentValue(dto.getCurrentValue());
        kpi.setReportingPeriod(dto.getReportingPeriod());
        kpi.setCategory(dto.getCategory());

        return kpi;
    }
    public void updateEntityFromDto(KPIDTO dto, KPI entity) {
        if (dto == null || entity == null) return;
        
        entity.setName(dto.getName());
        entity.setDefinition(dto.getDefinition());
        entity.setTarget(dto.getTarget());
        entity.setCurrentValue(dto.getCurrentValue());
        entity.setReportingPeriod(dto.getReportingPeriod());
        entity.setCategory(dto.getCategory());
    }
}
