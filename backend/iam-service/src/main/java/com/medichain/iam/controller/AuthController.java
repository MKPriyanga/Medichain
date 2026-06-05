package com.medichain.iam.controller;
 
import com.medichain.iam.dto.LoginRequestDTO;

import com.medichain.iam.dto.LoginResponseDTO;

import com.medichain.iam.dto.RegisterRequestDTO;

import com.medichain.iam.service.UserService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
 
@RestController

@RequestMapping("/api/auth")

@RequiredArgsConstructor

public class AuthController {
 
    private final UserService userService;
 
    @PostMapping("/register")

    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDTO dto) {

        userService.register(dto);

        return ResponseEntity.status(HttpStatus.CREATED)

                .body("Registration successful. Await admin approval.");

    }
 
    @PostMapping("/login")

    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {

        return ResponseEntity.ok(userService.login(dto));

    }

}
 