package com.cts.delivery_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deliveryId;

    @Column(nullable = false)
    private Integer requestId;

    @Column(nullable = false)
    private String deliveredBy;

    @CreationTimestamp
    private LocalDateTime deliveredAt;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String status;

    private Long warehouseId;
}