package com.cts.departmentrequest_service.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentRequestCreateDto {

    private Integer departmentId;
    private List<Integer> productIds;
    private Integer quantity;
}

