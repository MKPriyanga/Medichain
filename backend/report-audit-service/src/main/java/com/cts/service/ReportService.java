package com.cts.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.cts.client.billing.BillingIntegrationService;
import com.cts.client.billing.InvoiceDTO;
import com.cts.client.billing.PaymentDTO;
import com.cts.client.departmentRequest.DepartmentRequestDTO;
import com.cts.client.departmentRequest.DepartmentRequestIntegrationService;
import com.cts.client.delivery.DeliveryIntegrationService;
import com.cts.client.delivery.DeliveryRecordDTO;
import com.cts.client.inventory.InventoryIntegrationService;
import com.cts.client.inventory.InventoryItemDTO;
import com.cts.dto.ReportDTO;
import com.cts.entity.Report;
import com.cts.exception.InvalidRequestException;
import com.cts.mapper.ReportMapper;
import com.cts.repository.ReportRepository;
import jakarta.transaction.Transactional;

@Service
public class ReportService {

    private final ReportRepository                    reportRepository;
    private final ReportMapper                        reportMapper;
    private final DepartmentRequestIntegrationService requestIntegration;
    private final DeliveryIntegrationService          deliveryIntegration;
    private final BillingIntegrationService           billingIntegration;
    private final InventoryIntegrationService         inventoryIntegration;
    @Autowired
    public ReportService(ReportRepository reportRepository,
                         ReportMapper reportMapper,
                         DepartmentRequestIntegrationService requestIntegration,
                         DeliveryIntegrationService deliveryIntegration,
                         BillingIntegrationService billingIntegration,
                         InventoryIntegrationService inventoryIntegration) {
        this.reportRepository     = reportRepository;
        this.reportMapper         = reportMapper;
        this.requestIntegration   = requestIntegration;
        this.deliveryIntegration  = deliveryIntegration;
        this.billingIntegration   = billingIntegration;
        this.inventoryIntegration = inventoryIntegration;
    }

    public ReportDTO generateReport(String scope, String type,
                                    String status, Integer id,
                                    String generatedBy) {
        String resolvedType  = (type  == null || type.isBlank())  ? "ALL" : type.toUpperCase();
        String resolvedScope = (scope == null || scope.isBlank()) ? ""    : scope.toUpperCase();

        return switch (resolvedScope) {
            case "REQUESTS"               -> handleRequests(resolvedType, status, id, generatedBy);
            case "DELIVERIES", "DELIVERY" -> handleDeliveries(resolvedType, status, id, generatedBy);
            case "BILLING"                -> handleBilling(resolvedType, status, id, generatedBy);
            case "INVENTORY"              -> handleInventory(resolvedType, id, generatedBy);
            default -> throw new InvalidRequestException(
                "Invalid scope: \"" + scope + "\". Allowed: REQUESTS, DELIVERIES, BILLING, INVENTORY");
        };
    }

    // ── REQUESTS ─────────────────────────────────────────────────────────────

    private ReportDTO handleRequests(String type, String status, Integer id, String generatedBy) {
        return switch (type) {
            case "ALL" -> {
                List<DepartmentRequestDTO> all = requestIntegration.getAllRemoteRequests();
                long pending    = all.stream().filter(r -> "PENDING".equalsIgnoreCase(r.getStatus())).count();
                long approved   = all.stream().filter(r -> "APPROVED".equalsIgnoreCase(r.getStatus())).count();
                long rejected   = all.stream().filter(r -> "REJECTED".equalsIgnoreCase(r.getStatus())).count();
                long processing = all.stream().filter(r -> "PROCESSING".equalsIgnoreCase(r.getStatus())).count();
                long completed  = all.stream().filter(r -> "COMPLETED".equalsIgnoreCase(r.getStatus())).count();
                int  totalQty   = all.stream().mapToInt(r -> r.getQuantity() != null ? r.getQuantity() : 0).sum();
                String metrics = String.format(
                    "{ \"reportType\": \"ALL_REQUESTS\", \"totalRequests\": %d, " +
                    "\"pending\": %d, \"approved\": %d, \"rejected\": %d, " +
                    "\"processing\": %d, \"completed\": %d, " +
                    "\"totalQuantityRequested\": %d }",
                    all.size(), pending, approved, rejected, processing, completed, totalQty);
                yield buildAndSaveReport("REQUESTS", metrics, "requests_all", generatedBy);
            }
            case "STATUS" -> {
                if (status == null || status.isBlank())
                    throw new InvalidRequestException("status param is required when type=STATUS");
                long count = requestIntegration.getAllRemoteRequests().stream()
                    .filter(r -> status.equalsIgnoreCase(r.getStatus())).count();
                String metrics = String.format(
                    "{ \"reportType\": \"REQUESTS_BY_STATUS\", \"statusFilter\": \"%s\", " +
                    "\"matchingCount\": %d }",
                    status.toUpperCase(), count);
                yield buildAndSaveReport("REQUESTS", metrics, "requests_status_" + status, generatedBy);
            }
            case "ID" -> {
                if (id == null)
                    throw new InvalidRequestException("id param is required when type=ID");
                DepartmentRequestDTO r = requestIntegration.getRemoteRequestData(id);
                String metrics = String.format(
                    "{ \"reportType\": \"SINGLE_REQUEST\", \"requestId\": %d, " +
                    "\"departmentId\": %d, \"quantity\": %d, " +
                    "\"status\": \"%s\", \"approvedBy\": \"%s\" }",
                    id,
                    r.getDepartmentId() != null ? r.getDepartmentId() : 0,
                    r.getQuantity()     != null ? r.getQuantity()     : 0,
                    r.getStatus()       != null ? r.getStatus()       : "UNKNOWN",
                    r.getApprovedBy()   != null ? r.getApprovedBy()   : "N/A");
                yield buildAndSaveReport("REQUESTS", metrics, "requests_id_" + id, generatedBy);
            }
            default -> throw new InvalidRequestException(
                "Invalid type for REQUESTS. Allowed: ALL, STATUS, ID");
        };
    }

    // ── DELIVERIES ───────────────────────────────────────────────────────────

    private ReportDTO handleDeliveries(String type, String status, Integer id, String generatedBy) {
        return switch (type) {
            case "ALL" -> {
                List<DeliveryRecordDTO> all = deliveryIntegration.getAllDeliveries();
                long inTransit = all.stream().filter(d -> "IN_TRANSIT".equalsIgnoreCase(d.getStatus())).count();
                long closed    = all.stream().filter(d -> "CLOSED".equalsIgnoreCase(d.getStatus())).count();
                int  totalQty  = all.stream().mapToInt(d -> d.getQuantity() != null ? d.getQuantity() : 0).sum();
                String metrics = String.format(
                    "{ \"reportType\": \"ALL_DELIVERIES\", \"totalDeliveries\": %d, " +
                    "\"inTransit\": %d, \"closed\": %d, " +
                    "\"totalQuantityDelivered\": %d }",
                    all.size(), inTransit, closed, totalQty);
                yield buildAndSaveReport("DELIVERIES", metrics, "deliveries_all", generatedBy);
            }
            case "STATUS" -> {
                if (status == null || status.isBlank())
                    throw new InvalidRequestException("status param is required when type=STATUS");
                long count = deliveryIntegration.getAllDeliveries().stream()
                    .filter(d -> status.equalsIgnoreCase(d.getStatus())).count();
                String metrics = String.format(
                    "{ \"reportType\": \"DELIVERIES_BY_STATUS\", \"statusFilter\": \"%s\", " +
                    "\"matchingCount\": %d }",
                    status.toUpperCase(), count);
                yield buildAndSaveReport("DELIVERIES", metrics,
                    "deliveries_status_" + status, generatedBy);
            }
            case "ID" -> {
                if (id == null)
                    throw new InvalidRequestException("id param is required when type=ID");
                DeliveryRecordDTO d = deliveryIntegration.getDeliveryById(id);
                if (d == null)
                    throw new InvalidRequestException(
                        "Delivery not found or delivery-service unavailable for id: " + id);
                String metrics = String.format(
                    "{ \"reportType\": \"SINGLE_DELIVERY\", \"deliveryId\": %d, " +
                    "\"requestId\": %d, \"quantity\": %d, " +
                    "\"deliveredBy\": \"%s\", \"status\": \"%s\" }",
                    id,
                    d.getRequestId()   != null ? d.getRequestId()   : 0,
                    d.getQuantity()    != null ? d.getQuantity()    : 0,
                    d.getDeliveredBy() != null ? d.getDeliveredBy() : "N/A",
                    d.getStatus()      != null ? d.getStatus()      : "UNKNOWN");
                yield buildAndSaveReport("DELIVERIES", metrics,
                    "deliveries_id_" + id, generatedBy);
            }
            default -> throw new InvalidRequestException(
                "Invalid type for DELIVERIES. Allowed: ALL, STATUS, ID");
        };
    }

    // ── BILLING ──────────────────────────────────────────────────────────────

    private ReportDTO handleBilling(String type, String status, Integer id, String generatedBy) {
        return switch (type) {
            case "ALL" -> {
                List<InvoiceDTO> invoices = billingIntegration.getAllInvoices();
                List<PaymentDTO> payments = billingIntegration.getAllPayments();
                long paid       = invoices.stream().filter(i -> "PAID".equalsIgnoreCase(i.getStatus())).count();
                long unpaid     = invoices.stream().filter(i -> "UNPAID".equalsIgnoreCase(i.getStatus())).count();
                long paySuccess = payments.stream().filter(p -> "SUCCESS".equalsIgnoreCase(p.getStatus())).count();
                long payFailed  = payments.stream().filter(p -> "FAILED".equalsIgnoreCase(p.getStatus())).count();
                double totalInvoiceAmt = invoices.stream()
                    .mapToDouble(i -> i.getAmount() != null ? i.getAmount() : 0.0).sum();
                double totalCollected  = payments.stream()
                    .filter(p -> "SUCCESS".equalsIgnoreCase(p.getStatus()))
                    .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0).sum();
                double outstanding = totalInvoiceAmt - totalCollected;
                String metrics = String.format(
                    "{ \"reportType\": \"BILLING_SUMMARY\", \"totalInvoices\": %d, " +
                    "\"invoicePaid\": %d, \"invoiceUnpaid\": %d, " +
                    "\"totalPayments\": %d, \"paymentSuccess\": %d, \"paymentFailed\": %d, " +
                    "\"totalInvoiceAmount\": %.2f, \"totalCollected\": %.2f, " +
                    "\"outstanding\": %.2f }",
                    invoices.size(), paid, unpaid,
                    payments.size(), paySuccess, payFailed,
                    totalInvoiceAmt, totalCollected, outstanding);
                yield buildAndSaveReport("BILLING", metrics, "billing_all", generatedBy);
            }
            case "STATUS" -> {
                if (status == null || status.isBlank())
                    throw new InvalidRequestException(
                        "status param is required when type=STATUS. Use PAID or UNPAID");
                List<InvoiceDTO> filtered = billingIntegration.getAllInvoices().stream()
                    .filter(i -> status.equalsIgnoreCase(i.getStatus()))
                    .collect(Collectors.toList());
                double totalAmt = filtered.stream()
                    .mapToDouble(i -> i.getAmount() != null ? i.getAmount() : 0.0).sum();
                String metrics = String.format(
                    "{ \"reportType\": \"BILLING_BY_STATUS\", \"statusFilter\": \"%s\", " +
                    "\"matchingCount\": %d, \"totalAmount\": %.2f }",
                    status.toUpperCase(), filtered.size(), totalAmt);
                yield buildAndSaveReport("BILLING", metrics,
                    "billing_status_" + status.toLowerCase(), generatedBy);
            }
            case "ID" -> {
                if (id == null)
                    throw new InvalidRequestException("id param is required when type=ID");
                InvoiceDTO inv = billingIntegration.getInvoiceById(Long.valueOf(id));
                if (inv == null)
                    throw new InvalidRequestException("Invoice not found: " + id);
                String metrics = String.format(
                    "{ \"reportType\": \"SINGLE_INVOICE\", \"invoiceId\": %d, " +
                    "\"departmentId\": %d, \"amount\": %.2f, " +
                    "\"status\": \"%s\", \"issuedAt\": \"%s\", \"fileUri\": \"%s\" }",
                    inv.getInvoiceId()    != null ? inv.getInvoiceId()    : 0,
                    inv.getDepartmentId() != null ? inv.getDepartmentId() : 0,
                    inv.getAmount()       != null ? inv.getAmount()       : 0.0,
                    inv.getStatus()       != null ? inv.getStatus()       : "UNKNOWN",
                    inv.getIssuedAt()     != null ? inv.getIssuedAt()     : "N/A",
                    inv.getFileUri()      != null ? inv.getFileUri()      : "");
                yield buildAndSaveReport("BILLING", metrics, "billing_id_" + id, generatedBy);
            }
            default -> throw new InvalidRequestException(
                "Invalid type for BILLING. Allowed: ALL, STATUS, ID");
        };
    }

    // ── INVENTORY ────────────────────────────────────────────────────────────

    private ReportDTO handleInventory(String type, Integer id, String generatedBy) {
        return switch (type) {
            case "ALL" -> {
                List<InventoryItemDTO> all = inventoryIntegration.getAllInventory();
                long available  = all.stream().filter(i -> "AVAILABLE".equalsIgnoreCase(i.getStatus())).count();
                long outOfStock = all.stream().filter(i -> "OUT_OF_STOCK".equalsIgnoreCase(i.getStatus())).count();
                int  totalQty   = all.stream().mapToInt(i -> i.getQuantity() != null ? i.getQuantity() : 0).sum();
                String metrics = String.format(
                    "{ \"reportType\": \"INVENTORY_SUMMARY\", \"totalItems\": %d, " +
                    "\"available\": %d, \"outOfStock\": %d, " +
                    "\"totalQuantity\": %d }",
                    all.size(), available, outOfStock, totalQty);
                yield buildAndSaveReport("INVENTORY", metrics, "inventory_all", generatedBy);
            }
            case "WAREHOUSE" -> {
                if (id == null)
                    throw new InvalidRequestException("id (warehouseId) is required when type=WAREHOUSE");
                List<InventoryItemDTO> items = inventoryIntegration.getStockByWarehouse(Long.valueOf(id));
                int    totalQty  = items.stream().mapToInt(i -> i.getQuantity() != null ? i.getQuantity() : 0).sum();
                String wName     = items.isEmpty() ? "Unknown"
                    : (items.get(0).getWarehouse() != null ? items.get(0).getWarehouse().getName() : "Unknown");
                String wLocation = items.isEmpty() ? "N/A"
                    : (items.get(0).getWarehouse() != null ? items.get(0).getWarehouse().getLocation() : "N/A");
                String metrics = String.format(
                    "{ \"reportType\": \"INVENTORY_BY_WAREHOUSE\", \"warehouseId\": %d, " +
                    "\"warehouseName\": \"%s\", \"location\": \"%s\", " +
                    "\"itemCount\": %d, \"totalQuantity\": %d }",
                    id, wName, wLocation, items.size(), totalQty);
                yield buildAndSaveReport("INVENTORY", metrics,
                    "inventory_warehouse_" + id, generatedBy);
            }
            default -> throw new InvalidRequestException(
                "Invalid type for INVENTORY. Allowed: ALL, WAREHOUSE");
        };
    }

    // ── CRUD helpers ─────────────────────────────────────────────────────────

    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
    }

    public ReportDTO getReportDTOById(Long id) {
        return reportMapper.toDTO(getReportById(id));
    }

    public List<ReportDTO> getAllReportsDTO() {
        return reportRepository.findAll().stream()
                .map(reportMapper::toDTO)
                .collect(Collectors.toList());
    }

    public String getFileName(Long id) {
        return getReportById(id).getFileName();
    }

    public void deleteReportById(Long id) {
        if (!reportRepository.existsById(id))
            throw new RuntimeException("Report not found: " + id);
        reportRepository.deleteById(id);
    }

    @Transactional
    public void deleteReportByFileName(String fileName) {
        reportRepository.deleteByFileName(fileName);
    }

    @Retryable(
        value = { Exception.class },
        maxAttemptsExpression = "${report.retry.maxAttempts}",
        backoff = @Backoff(delayExpression = "${report.retry.delay}")
    )
    public ReportDTO createReport(ReportDTO reportDTO) {
        if (reportDTO.getFileName() == null || reportDTO.getFileName().isEmpty())
            throw new IllegalArgumentException("File name cannot be empty for compliance.");
        return reportMapper.toDTO(reportRepository.save(reportMapper.toEntity(reportDTO)));
    }

    @Recover
    public ReportDTO recoverCreateReport(Exception e, ReportDTO reportDTO) {
        System.err.println("Database retry failed for Report: " + reportDTO.getFileName());
        return null;
    }

    public ReportDTO generateReportByScope(String scope, String generatedBy) {
        return generateReport(scope, "ALL", null, null, generatedBy);
    }

    /**
     * Build the report content, write it to local storage, compute SHA-256,
     * then persist the record with fileUri + sha256Hash.
     */
    private ReportDTO buildAndSaveReport(String scope, String metrics,
                                         String filePrefix, String generatedBy) {
        long   timestamp = System.currentTimeMillis();
        String fileName  = filePrefix.toLowerCase() + "_" + timestamp + ".json";

        // Build the full file content
        String content = String.format(
            "{ \"scope\": \"%s\", \"generatedBy\": \"%s\", " +
            "\"generatedAt\": \"%s\", \"metrics\": %s }",
            scope, generatedBy, LocalDateTime.now(), metrics);

        ReportDTO dto = new ReportDTO();
        dto.setScope(scope);
        dto.setGeneratedBy(generatedBy);
        dto.setGeneratedAt(LocalDateTime.now());
        dto.setFileName(fileName);
        dto.setParametersJSON(
            "{ \"scope\": \"" + scope + "\", \"generatedBy\": \"" + generatedBy + "\" }");
        dto.setMetricsJSON(metrics);
        return this.createReport(dto);
    }
}