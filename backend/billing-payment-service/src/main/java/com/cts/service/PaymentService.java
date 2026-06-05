package com.cts.service;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.entity.Invoice;
import com.cts.entity.Payment;
import com.cts.exception.InvalidRequestException;
import com.cts.feign.NotificationClient;
import com.cts.feign.NotificationRequestDto;
import com.cts.repository.InvoiceRepository;
import com.cts.repository.PaymentRepository;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final InvoiceRepository invoiceRepo;
    private final NotificationClient notificationClient;


    public PaymentService(PaymentRepository paymentRepo,
                          InvoiceRepository invoiceRepo,
                          NotificationClient notificationClient) {
        this.paymentRepo = paymentRepo;
        this.invoiceRepo = invoiceRepo;
        this.notificationClient      = notificationClient;
    }

    public Payment processPayment(Payment payment) {
        // ── Your original logic, untouched ──
        Invoice invoice = invoiceRepo.findById(payment.getInvoiceId())
                .orElseThrow(() ->
                        new InvalidRequestException("Invoice not found for payment"));

        if ("PAID".equals(invoice.getStatus())) {
            throw new InvalidRequestException("Invoice already paid");
        }
        if (payment.getAmount() <= 0) {
            throw new InvalidRequestException("Payment amount must be > 0");
        }

        payment.setStatus("SUCCESS");
        Payment saved = paymentRepo.save(payment);

        invoice.setStatus("PAID");
        invoiceRepo.save(invoice);

        if (invoice.getDepartmentId() != null) {
            notificationClient.sendNotification(new NotificationRequestDto(
                invoice.getDepartmentId(),
                saved.getPaymentId(),
                "Payment #" + saved.getPaymentId() + " of amount " + saved.getAmount() +
                " recorded for invoice #" + invoice.getInvoiceId() + ". Invoice is now PAID.",
                "BILLING"
            ));
        }
 
        return saved;
    }

    public List<Payment> getAllPayments() {
        return paymentRepo.findAll(); // your logic
    }
}
