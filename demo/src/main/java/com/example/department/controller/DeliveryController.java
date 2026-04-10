package com.example.department.controller;

import com.example.department.entity.DeliveryRecord;
import com.example.department.service.DeliveryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @PostMapping("/deliveries")
    public ResponseEntity<String> createDelivery(
            @RequestHeader("Role") String role,
            @RequestBody Map<String, Object> body) {

        deliveryService.createDelivery(role, body);
        return ResponseEntity.ok("Delivery record created successfully");
    }

    @GetMapping("/deliveries")
    public ResponseEntity<List<DeliveryRecord>> listDeliveries(
            @RequestHeader("Role") String role,
            @RequestParam(required = false) Integer requestId) {

        return ResponseEntity.ok(
                deliveryService.listDeliveries(role, requestId));
    }

    @PostMapping("/proof-of-receipt")
    public ResponseEntity<String> uploadProof(
            @RequestHeader("Role") String role,
            @RequestBody Map<String, Object> body) {

        deliveryService.uploadProof(role, body);
        return ResponseEntity.ok("Proof of receipt uploaded successfully");
    }

    @PutMapping("/deliveries/{deliveryId}/close")
    public ResponseEntity<String> closeRequest(
            @RequestHeader("Role") String role,
            @PathVariable Integer deliveryId) {

        deliveryService.closeRequest(role, deliveryId);
        return ResponseEntity.ok("Request closed successfully");
    }
}
