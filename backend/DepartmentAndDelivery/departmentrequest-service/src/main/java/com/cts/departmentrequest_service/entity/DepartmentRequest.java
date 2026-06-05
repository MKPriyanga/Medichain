package com.cts.departmentrequest_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "department_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer requestId;

    @Column(name = "department_id", nullable = false)
    private Integer departmentId;

    @Column(name = "product_ids_json", columnDefinition = "TEXT")
    private String productIdsJson;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String status;

    @CreationTimestamp
    @Column(name = "requested_at", updatable = false)
    private LocalDateTime requestedAt;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_by_user_id")
    private Long createdByUserId;
}
