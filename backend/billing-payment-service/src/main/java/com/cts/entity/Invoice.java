package com.cts.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    private Long requestId;
    private Long departmentId;

    @Min(value = 1, message = "Amount must be greater than zero")
    private Double amount;

    @CreationTimestamp
    private LocalDateTime issuedAt;

    @NotBlank
    private String status; // UNPAID, PAID

    private String fileUri;
}