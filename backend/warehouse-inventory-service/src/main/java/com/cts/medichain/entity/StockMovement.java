package com.cts.medichain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movementId;

    private Long itemId;         // references the source InventoryItem
    private Long productId;
    private Long fromWarehouseId;
    private Long toWarehouseId;
    private Integer quantity;

    @CreationTimestamp
    private LocalDateTime performedAt;

    private String status;
}