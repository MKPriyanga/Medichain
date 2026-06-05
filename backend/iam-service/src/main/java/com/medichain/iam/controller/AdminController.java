package com.medichain.iam.controller;
 
import com.medichain.iam.dto.AssignRoleRequestDTO;

import com.medichain.iam.service.AdminUserService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;
 
@RestController

@RequestMapping("/api/admin/users")

@RequiredArgsConstructor

@PreAuthorize("hasRole('ADMIN')")

public class AdminController {
 
    private final AdminUserService adminUserService;
 


    @PutMapping("/{id}/activate")

    public ResponseEntity<String> activate(@PathVariable Long id) {

        adminUserService.activateUser(id);

        return ResponseEntity.ok("User activated successfully");

    }
 
   

    @PutMapping("/{id}/deactivate")

    public ResponseEntity<String> deactivate(@PathVariable Long id) {

        adminUserService.deactivateUser(id);

        return ResponseEntity.ok("User deactivated successfully");

    }
 
  

    @PutMapping("/{id}/role")

    public ResponseEntity<String> assignRole(

            @PathVariable Long id,

            @Valid @RequestBody AssignRoleRequestDTO dto) {

        adminUserService.assignRole(id, dto.getRole());

        return ResponseEntity.ok("Role assigned successfully");

    }

}
 