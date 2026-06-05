package com.cts;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallBackController {

    @RequestMapping("/iam")
    public ResponseEntity<Map<String, String>> iamFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "SERVICE_UNAVAILABLE",
                        "message", "IAM Service is currently unavailable.",
                        "service", "iam-service"
                ));
    }

    @RequestMapping("/notification")
    public ResponseEntity<Map<String, String>> notificationFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "SERVICE_UNAVAILABLE",
                        "message", "Notification Service is currently unavailable.",
                        "service", "notification-service"
                ));
    }

    @RequestMapping("/report")
    public ResponseEntity<Map<String, String>> reportFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "SERVICE_UNAVAILABLE",
                        "message", "Report-Audit Service is currently unavailable.",
                        "service", "report-audit-service"
                ));
    }

    @RequestMapping("/kpi")
    public ResponseEntity<Map<String, String>> kpiFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "SERVICE_UNAVAILABLE",
                        "message", "KPI Service is currently unavailable.",
                        "service", "kpi-service"
                ));
    }

    @RequestMapping("/department-request")
    public ResponseEntity<Map<String, String>> departmentRequestFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "SERVICE_UNAVAILABLE",
                        "message", "Department Request Service is currently unavailable.",
                        "service", "departmentrequest-service"
                ));
    }

    @RequestMapping("/delivery")
    public ResponseEntity<Map<String, String>> deliveryFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "SERVICE_UNAVAILABLE",
                        "message", "Delivery Service is currently unavailable.",
                        "service", "delivery-service"
                ));
    }
    @RequestMapping("/department")
    public ResponseEntity<Map<String, String>> departmentFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "SERVICE_UNAVAILABLE",
                        "message", "Department Service is currently unavailable.",
                        "service", "department-service"
                ));
    }
    
    @RequestMapping("/product")
    public ResponseEntity<Map<String, String>> productFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "SERVICE_UNAVAILABLE",
                        "message", "Product & Supplier Service is currently unavailable.",
                        "service", "product-supplier-service"));
    }
    
    @RequestMapping("/warehouse")
    public ResponseEntity<Map<String, String>> warehouseFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "SERVICE_UNAVAILABLE",
                        "message", "Warehouse & Inventory Service is currently unavailable.",
                        "service", "warehouse-inventory-service"));
    }
    
    @RequestMapping("/billing")
    public ResponseEntity<Map<String, String>> billingFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "SERVICE_UNAVAILABLE",
                        "message", "Billing & Payment Service is currently unavailable.",
                        "service", "billing-payment-service"));
    }
}