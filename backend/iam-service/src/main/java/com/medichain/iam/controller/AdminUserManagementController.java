package com.medichain.iam.controller;
 
import com.medichain.iam.dto.CreateUserRequestDTO;

import com.medichain.iam.dto.UserResponseDTO;

import com.medichain.iam.service.UserService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController

@RequestMapping("/api/admin/user-management")

@RequiredArgsConstructor

public class AdminUserManagementController {
 
    private final UserService userService;
 
    @PreAuthorize("hasRole('ADMIN')")

    @PostMapping

    public ResponseEntity<String> createUser(@Valid @RequestBody CreateUserRequestDTO dto) {

        userService.createUserByAdmin(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");

    }
 
    @PreAuthorize("hasRole('ADMIN')")

    @GetMapping

    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

        return ResponseEntity.ok(userService.getAllUsers());

    }
 
    @PreAuthorize("hasRole('ADMIN')")

    @GetMapping("/{id}")

    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {

        return ResponseEntity.ok(userService.getUserById(id));

    }

}
 