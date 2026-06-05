package com.cts.client.billing;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class BillingIntegrationService {

    private final BillingClient billingClient;

    public BillingIntegrationService(BillingClient billingClient) {
        this.billingClient = billingClient;
    }

    public List<InvoiceDTO> getAllInvoices() {
        return billingClient.getAllInvoices();
    }

    public InvoiceDTO getInvoiceById(Long id) {
        return billingClient.getInvoiceById(id);
    }

    public List<PaymentDTO> getAllPayments() {
        return billingClient.getAllPayments();
    }
}