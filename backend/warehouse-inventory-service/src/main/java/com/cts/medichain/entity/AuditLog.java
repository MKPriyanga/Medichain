package com.cts.medichain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    @Column(nullable = false)
    private String username;

    private String action;
    private String resourceType;
    private Long resourceId;

    @Column(length = 2000)
    private String details;

    @CreationTimestamp
    private LocalDateTime timestamp;
}