package com.medichain.iam.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "audit_logs")
//, indexes = {
//        @Index(name = "idx_audit_user_id",  columnList = "userId"),
//        @Index(name = "idx_audit_resource", columnList = "resourceType, resourceId"),
//        @Index(name = "idx_audit_ts",       columnList = "timestamp")
//})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String action;        

    @Column(nullable = false)
    private String resourceType;  

    private String resourceId;    

    @Column(columnDefinition = "TEXT")
    private String details;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime timestamp;
}