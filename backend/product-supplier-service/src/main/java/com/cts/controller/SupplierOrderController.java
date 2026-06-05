package com.cts.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.dto.ReceiveOrderDto;
import com.cts.entity.SupplierOrder;
import com.cts.service.SupplierOrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class SupplierOrderController {

    private final SupplierOrderService orderService;

    public SupplierOrderController(SupplierOrderService orderService) {
        this.orderService = orderService;
    }

    // LIST ALL ORDERS — all authenticated roles
    @GetMapping
    public List<SupplierOrder> listOrders() {
        return orderService.getAllOrders();
    }

    // PLACE ORDER — PROCUREMENT / ADMIN
    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestBody SupplierOrder order,
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

        if (role != null && !"PROCUREMENT".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Only PROCUREMENT or ADMIN can place orders.");
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(order));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // RECEIVE ORDER — WAREHOUSE / ADMIN: marks PLACED → RECEIVED and restocks inventory
    @PutMapping("/{orderId}/receive")
    public ResponseEntity<?> receiveOrder(
            @PathVariable Long orderId,
            @RequestBody ReceiveOrderDto dto,
            @RequestHeader(value = "X-Auth-Role", required = false) String role) {

        if (role != null && !"WAREHOUSE".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Only WAREHOUSE or ADMIN can receive orders.");
        }
        try {
            return ResponseEntity.ok(orderService.receiveOrder(orderId, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
