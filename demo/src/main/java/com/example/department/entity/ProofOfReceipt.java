package com.example.department.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "proof_of_receipt")
public class ProofOfReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer proofId;

    @Column(name = "delivery_id", nullable = false)
    private Integer deliveryId;

    @Column(name = "department_id", nullable = false)
    private Integer departmentId;

    @Column(name = "file_uri", nullable = false)
    private String fileUri;
    
    
        @Column(name = "received_at")
        private LocalDateTime receivedAt = LocalDateTime.now();

        @Column(nullable = false)
        private String status;

        /* ✅ REQUIRED GETTERS & SETTERS */

        public Integer getProofId() {
            return proofId;
        }

        public Integer getDeliveryId() {
            return deliveryId;
        }

        public void setDeliveryId(Integer deliveryId) {
            this.deliveryId = deliveryId;
        }

        public Integer getDepartmentId() {
            return departmentId;
        }

        public void setDepartmentId(Integer departmentId) {
            this.departmentId = departmentId;
        }

        public String getFileUri() {
            return fileUri;
        }

        public void setFileUri(String fileUri) {
            this.fileUri = fileUri;
        }

        public LocalDateTime getReceivedAt() {
            return receivedAt;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
