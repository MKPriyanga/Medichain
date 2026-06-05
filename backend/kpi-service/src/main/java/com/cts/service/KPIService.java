package com.cts.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.client.KPIDeliveryClient;
import com.cts.client.KPIRequestClient;
import com.cts.dto.KPIDTO;
import com.cts.entity.KPI;
import com.cts.mapper.KPIMapper;
import com.cts.repository.KPIRepository;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class KPIService {

    private final KPIRepository     kpiRepository;
    private final KPIMapper         kpiMapper;
    private final KPIRequestClient  kpiRequestClient;
    private final KPIDeliveryClient kpiDeliveryClient;

    @Autowired
    public KPIService(KPIRepository kpiRepository,
                      KPIMapper kpiMapper,
                      KPIRequestClient kpiRequestClient,
                      KPIDeliveryClient kpiDeliveryClient) {
        this.kpiRepository    = kpiRepository;
        this.kpiMapper        = kpiMapper;
        this.kpiRequestClient = kpiRequestClient;
        this.kpiDeliveryClient = kpiDeliveryClient;
    }

    // ── CRUD ─────────────────────────────────────────────────────────────

    public List<KPIDTO> getAllKPIs() {
        return kpiRepository.findAll().stream()
                .map(kpiMapper::toDTO).collect(Collectors.toList());
    }

    public List<KPIDTO> getKPIsByCategory(String category) {
        return kpiRepository.findByCategory(category).stream()
                .map(kpiMapper::toDTO).collect(Collectors.toList());
    }

    @Recover
    public KPIDTO recoverKPITask(Exception e, KPIDTO kpiDto) {
        return null;
    }

    // ── SYNC ─────────────────────────────────────────────────────────────

    @Transactional
    public void syncRemoteKpis() {
        syncFulfillmentKpi();
        syncCompletionKpi();
        syncUtilizationKpi();
    }

    // Request Fulfillment Rate = (APPROVED / total) × 100
    private void syncFulfillmentKpi() {
        try {
            List<JsonNode> requests = kpiRequestClient.getAllRequests();
            if (requests == null || requests.isEmpty()) return;

            long total    = requests.size();
            long approved = requests.stream()
                .filter(r -> "APPROVED".equalsIgnoreCase(getStr(r, "status"))).count();

            double rate = total > 0
                ? Math.round((approved * 100.0 / total) * 100.0) / 100.0 : 0.0;

            upsertKpi(
                "Request Fulfillment Rate",
                String.format("Approved %d / Total %d requests = %.2f%%", approved, total, rate),
                "REQUESTS", rate + "%", "monthly"
            );
        } catch (Exception ignored) {}
    }

    // Delivery Completion Rate = (CLOSED / total) × 100
    private void syncCompletionKpi() {
        try {
            List<JsonNode> deliveries = kpiDeliveryClient.getAllDeliveries();
            if (deliveries == null || deliveries.isEmpty()) return;

            long total  = deliveries.size();
            long closed = deliveries.stream()
                .filter(d -> "CLOSED".equalsIgnoreCase(getStr(d, "status"))).count();

            double rate = total > 0
                ? Math.round((closed * 100.0 / total) * 100.0) / 100.0 : 0.0;

            upsertKpi(
                "Delivery Completion Rate",
                String.format("Closed %d / Total %d deliveries = %.2f%%", closed, total, rate),
                "DELIVERY", rate + "%", "monthly"
            );
        } catch (Exception ignored) {}
    }

    // Stock Utilization Rate = (CLOSED deliveries / APPROVED requests) × 100
    private void syncUtilizationKpi() {
        try {
            List<JsonNode> requests   = kpiRequestClient.getAllRequests();
            List<JsonNode> deliveries = kpiDeliveryClient.getAllDeliveries();
            if (requests == null || deliveries == null) return;

            long approvedRequests = requests.stream()
                .filter(r -> "APPROVED".equalsIgnoreCase(getStr(r, "status"))).count();
            long closedDeliveries = deliveries.stream()
                .filter(d -> "CLOSED".equalsIgnoreCase(getStr(d, "status"))).count();

            double rate = approvedRequests > 0
                ? Math.round((closedDeliveries * 100.0 / approvedRequests) * 100.0) / 100.0 : 0.0;

            upsertKpi(
                "Stock Utilization Rate",
                String.format("Closed deliveries %d / Approved requests %d = %.2f%%",
                    closedDeliveries, approvedRequests, rate),
                "INVENTORY", rate + "%", "monthly"
            );
        } catch (Exception ignored) {}
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private String getStr(JsonNode node, String field) {
        if (node == null || !node.has(field) || node.get(field).isNull()) return "";
        return node.get(field).asText("");
    }

    private void upsertKpi(String name, String definition, String category,
                           String currentValue, String period) {
        KPI kpi = kpiRepository.findByName(name).orElseGet(() -> {
            KPI k = new KPI();
            k.setName(name);
            k.setCategory(category);
            k.setTarget("95%");
            k.setReportingPeriod(period);
            return k;
        });
        kpi.setDefinition(definition);
        kpi.setCurrentValue(currentValue);
        kpiRepository.save(kpi);
    }
}