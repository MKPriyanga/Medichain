package com.cts.department_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "department_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Department {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "department_id")
	    private Integer departmentId;

	    @Column(name = "department_name", nullable = false)
	    private String name;

	    @Column(name = "head_id")
	    private Integer headId;

	    @Column(name = "contact_info")
	    private String contactInfo;

	    @Column(nullable = false)
	    private String status; // ACTIVE / INACTIVE

	}



