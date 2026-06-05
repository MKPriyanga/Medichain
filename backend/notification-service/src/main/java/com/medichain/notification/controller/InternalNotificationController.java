package com.medichain.notification.controller;

import com.medichain.notification.dto.NotificationRequestDTO;
import com.medichain.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/internal/notifications")
@RequiredArgsConstructor
public class InternalNotificationController {

    private final NotificationService service;

    @PostMapping
    public ResponseEntity<String> sendNotification(
            @Valid @RequestBody NotificationRequestDTO dto) {
        service.sendNotification(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Notification created");
    }
}