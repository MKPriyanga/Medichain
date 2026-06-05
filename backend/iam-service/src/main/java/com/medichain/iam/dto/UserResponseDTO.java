package com.medichain.iam.dto;

import com.medichain.iam.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private boolean active;
}