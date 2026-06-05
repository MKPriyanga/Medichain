package com.medichain.iam.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignRoleRequestDTO {

    @NotBlank(message = "Role is required")
    private String role;
}