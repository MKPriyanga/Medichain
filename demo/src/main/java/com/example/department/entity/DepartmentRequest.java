package com.example.department.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "department_request")
public class DepartmentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requestId;

    @Column(name = "department_id", nullable = false)
    private Integer departmentId;

    @Column(name = "request_data", columnDefinition = "TEXT", nullable = false)
    private String requestData;

    @Column(nullable = false)
    private String status;

    // getters & setters

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}