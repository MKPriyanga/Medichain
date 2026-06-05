package com.cts.client.billing;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "billing-payment-service", fallback = BillingClientFallback.class)
public interface BillingClient {

    @GetMapping("/api/invoices")
    List<InvoiceDTO> getAllInvoices();

    @GetMapping("/api/invoices/{id}")
    InvoiceDTO getInvoiceById(@PathVariable Long id);

    @GetMapping("/api/payments")
    List<PaymentDTO> getAllPayments();
}