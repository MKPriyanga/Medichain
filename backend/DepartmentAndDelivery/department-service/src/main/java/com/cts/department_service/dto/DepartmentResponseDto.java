package com.cts.department_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponseDto {

    private Integer departmentId;
    private String name;
    private Integer headId;
    private String contactInfo;
    private String status;
}

