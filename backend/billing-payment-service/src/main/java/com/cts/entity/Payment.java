package com.cts.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private Long invoiceId;
    private Double amount;

    @NotBlank
    private String method; // CREDIT_CARD, UPI, CASH

    @CreationTimestamp
    private LocalDateTime paidAt;

    private String status; // SUCCESS, FAILED
}