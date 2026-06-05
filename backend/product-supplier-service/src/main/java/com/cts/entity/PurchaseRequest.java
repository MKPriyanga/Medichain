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
public class PurchaseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseRequestId;

    private Long productId;
    private Integer quantity;

    // Optional: reference to the originating Department Request
    private Long departmentRequestId;

    // Which warehouse is raising this request
    private Long warehouseId;

    // The Warehouse operator's user ID (from X-Auth-UserId header)
    private Long createdByUserId;

    @Column(length = 500)
    private String notes;

    // PENDING → ORDERED (when Procurement approves)
    // PENDING → REJECTED (when Procurement rejects)
    private String status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Populated when Procurement approves and a SupplierOrder is created
    private Long orderId;

    // Procurement officer who reviewed this
    private Long reviewedByUserId;

    private LocalDateTime reviewedAt;
}
