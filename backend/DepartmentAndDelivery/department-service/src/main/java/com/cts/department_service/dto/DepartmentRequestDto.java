package com.cts.department_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentRequestDto {

    @NotBlank
    private String name;

    private Integer headId;

    private String contactInfo;

    @NotBlank
    private String status;
}

