package com.cts.entity;



import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Long supplierId;

    @Column(columnDefinition = "TEXT")
    private String productIdsJson;

    private Integer quantity;

    @CreationTimestamp
    private LocalDateTime orderedAt;

    private String status; // PLACED / RECEIVED / CANCELLED

    private LocalDateTime receivedAt;
}
