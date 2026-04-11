package com.example.department.dto;

public class DepartmentDTO {

    private Integer departmentId;
    private String name;

    // ✅ REQUIRED by Jackson
    public DepartmentDTO() {
    }

    // ✅ Used for response
    public DepartmentDTO(Integer departmentId, String name) {
        this.departmentId = departmentId;
        this.name = name;
    }

    // ✅ getters & setters

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}