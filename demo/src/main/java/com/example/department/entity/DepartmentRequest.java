//package com.example.department.entity;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "department_request")
//public class DepartmentRequest {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer requestId;
//
//    @Column(name = "department_id", nullable = false)
//    private Integer departmentId;
//
//    @Column(name = "request_data", columnDefinition = "TEXT", nullable = false)
//    private String requestData;
//
//    @Column(nullable = false)
//    private String status;
//
//    // getters & setters
//
//    public Integer getRequestId() {
//        return requestId;
//    }
//
//    public void setRequestId(Integer requestId) {
//        this.requestId = requestId;
//    }
//
//    public Integer getDepartmentId() {
//        return departmentId;
//    }
//
//    public void setDepartmentId(Integer departmentId) {
//        this.departmentId = departmentId;
//    }
//
//    public String getRequestData() {
//        return requestData;
//    }
//
//    public void setRequestData(String requestData) {
//        this.requestData = requestData;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//}
package com.example.department.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "department_request")
public class DepartmentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer requestId;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "request_data", columnDefinition = "TEXT")
    private String requestData;

    @Column(nullable = false)
    private Integer quantity;                // ✅ NEW COLUMN

    @Column(nullable = false)
    private String status;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;        // ✅ NEW COLUMN

    @Column(name = "approved_by")
    private Integer approvedBy;               // ✅ NEW COLUMN

    // ✅ getters & setters

    public Integer getRequestId() {
        return requestId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Integer getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }
}