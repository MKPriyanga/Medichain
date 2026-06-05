package com.cts.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cts.entity.Invoice;
import com.cts.exception.InvalidRequestException;
import com.cts.feign.DepartmentRequestDTO;
import com.cts.feign.DepartmentRequestValidationClient;
import com.cts.feign.NotificationClient;
import com.cts.feign.NotificationRequestDto;
import com.cts.feign.WarehouseInventoryClient;
import com.cts.repository.InvoiceRepository;

@Service
public class InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceService.class);

    private final InvoiceRepository invoiceRepo;
    private final DepartmentRequestValidationClient requestValidationClient;
    private final NotificationClient notificationClient;
    private final WarehouseInventoryClient warehouseInventoryClient;

    public InvoiceService(InvoiceRepository invoiceRepo,
                          DepartmentRequestValidationClient requestValidationClient,
                          NotificationClient notificationClient,
                          WarehouseInventoryClient warehouseInventoryClient) {
        this.invoiceRepo              = invoiceRepo;
        this.requestValidationClient  = requestValidationClient;
        this.notificationClient       = notificationClient;
        this.warehouseInventoryClient = warehouseInventoryClient;
    }

    public Invoice createInvoice(Invoice invoice) {
        if (invoice.getAmount() == null || invoice.getAmount() <= 0)
            throw new InvalidRequestException("Invoice amount must be > 0");

        if (invoice.getStatus() == null || invoice.getStatus().trim().isEmpty())
            invoice.setStatus("UNPAID");

        if (invoice.getRequestId() != null) {
            DepartmentRequestDTO request =
                requestValidationClient.getRequestById(invoice.getRequestId());
            if (request == null)
                throw new InvalidRequestException(
                    "Cannot create invoice: departmentrequest-service unavailable. Try again shortly.");
            if (!"APPROVED".equalsIgnoreCase(request.getStatus()))
                throw new InvalidRequestException(
                    "Cannot create invoice: request " + invoice.getRequestId()
                    + " is not APPROVED (status=" + request.getStatus() + ")");
        }

        Invoice saved = invoiceRepo.save(invoice);

        if (saved.getDepartmentId() != null) {
            notificationClient.sendNotification(new NotificationRequestDto(
                saved.getDepartmentId(), saved.getInvoiceId(),
                "Invoice #" + saved.getInvoiceId() + " of ₹" + saved.getAmount()
                + " has been issued. Status: UNPAID.", "BILLING"));
        }

        return saved;
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepo.findAll();
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepo.findById(id)
                .orElseThrow(() -> new InvalidRequestException("Invoice not found: " + id));
    }
}