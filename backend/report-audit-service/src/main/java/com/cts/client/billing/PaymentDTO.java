package com.cts.client.billing;

import lombok.Data;

@Data
public class PaymentDTO {
    private Long paymentId;
    private Long invoiceId;
    private Double amount;
    private String method;
    private String status;
    private String paidAt;
}