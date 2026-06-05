package com.cts.client.billing;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BillingClientFallback implements BillingClient {

    private static final Logger log = LoggerFactory.getLogger(BillingClientFallback.class);

    @Override
    public List<InvoiceDTO> getAllInvoices() {
        log.warn("billing-payment-service unavailable — returning empty invoice list");
        return Collections.emptyList();
    }

    @Override
    public InvoiceDTO getInvoiceById(Long id) {
        log.warn("billing-payment-service unavailable — cannot fetch invoice id={}", id);
        return null;
    }

    @Override
    public List<PaymentDTO> getAllPayments() {
        log.warn("billing-payment-service unavailable — returning empty payment list");
        return Collections.emptyList();
    }
}