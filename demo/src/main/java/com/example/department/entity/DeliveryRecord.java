package com.example.department.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_record")
public class DeliveryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deliveryId;

    @Column(name = "request_id", nullable = false)
    private Integer requestId;

    @Column(name = "delivered_by", nullable = false)
    private String deliveredBy;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt = LocalDateTime.now();

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String status;

    /* Getters & Setters */

    public Integer getDeliveryId() { return deliveryId; }
    public void setDeliveryId(Integer deliveryId) { this.deliveryId = deliveryId; }

    public Integer getRequestId() { return requestId; }
    public void setRequestId(Integer requestId) { this.requestId = requestId; }

    public String getDeliveredBy() { return deliveredBy; }
    public void setDeliveredBy(String deliveredBy) { this.deliveredBy = deliveredBy; }

    public LocalDateTime getDeliveredAt() { return deliveredAt; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}