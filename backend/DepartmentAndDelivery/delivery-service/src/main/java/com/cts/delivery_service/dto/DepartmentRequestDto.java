package com.cts.delivery_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentRequestDto {

    private Integer requestId;
    private Integer departmentId;
    private String productIdsJson;
    private Integer quantity;
    private String status;
    private Integer headId;
}
