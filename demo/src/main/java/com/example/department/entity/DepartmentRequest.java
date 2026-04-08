package com.example.department.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "department_request")
public class DepartmentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "request_data")
    private String requestData;

    @Column(name = "status")
    private String status;

    // getters and setters

    public Long getRequestId() {
        return requestId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
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