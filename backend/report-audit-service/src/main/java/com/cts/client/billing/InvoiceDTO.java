package com.cts.client.billing;

import lombok.Data;

@Data
public class InvoiceDTO {
    private Long invoiceId;
    private Long requestId;
    private Long departmentId;
    private Double amount;
    private String status;
    private String issuedAt;
    private String fileUri;
}