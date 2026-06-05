package com.cts.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentRequestDTO {
    private Integer requestId;
    private Integer departmentId;
    private Integer quantity;
    private String status;      
    private String approvedBy;
}