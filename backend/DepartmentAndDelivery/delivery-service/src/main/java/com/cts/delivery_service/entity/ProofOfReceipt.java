package com.cts.delivery_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "proof_of_receipt")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProofOfReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer proofId;

    @Column(nullable = false)
    private Integer deliveryId;

    @Column(nullable = false)
    private Integer departmentId;

    @CreationTimestamp
    @Column(name = "received_at", updatable = false)
    private LocalDateTime receivedAt;

    @Column(name = "file_uri")
    private String fileUri;

    @Column(nullable = false)
    private String status; // RECEIVED / REJECTED
}