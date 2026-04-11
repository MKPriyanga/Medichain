//package com.example.department.entity;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "department_master")
//public class Department {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "department_id")
//    private Integer departmentId;
//
//    @Column(name="department_name",nullable = false)
//    private String name;
//
//    // getters & setters
//
//    public Integer getDepartmentId() {
//        return departmentId;
//    }
//
//    public void setDepartmentId(Integer departmentId) {
//        this.departmentId = departmentId;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//}
package com.example.department.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "department_master")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "department_name", nullable = false)
    private String name;

    // ✅ NEW COLUMNS (added, but not mandatory to use immediately)

    @Column(name = "head_id")
    private Integer headId;

    @Column(name = "contact_info")
    private String contactInfo;

    @Column(nullable = false)
    private String status; // ACTIVE / INACTIVE

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

    public Integer getHeadId() {
        return headId;
    }

    public void setHeadId(Integer headId) {
        this.headId = headId;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
