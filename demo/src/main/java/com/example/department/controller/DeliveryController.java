package com.example.department.controller;

import com.example.department.exception.InvalidRequestException;
import com.example.department.exception.UnauthorizedRoleException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class DeliveryController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @GetMapping("/deliveries")
    public ResponseEntity<?> listDeliveries(
            @RequestHeader(value = "Role", required = false) String role,
            @RequestParam(value = "requestId", required = false) Integer requestId) {

        /* ========= ROLE VALIDATION ========= */
        if (role == null || role.trim().isEmpty() ||
                (!"ADMIN".equalsIgnoreCase(role.trim())
                 && !"STORE".equalsIgnoreCase(role.trim())
                 && !"DEPARTMENT_HEAD".equalsIgnoreCase(role.trim()))) {

            throw new UnauthorizedRoleException(
                    "You are not authorized to view delivery records");
        }

        /* ========= FETCH DELIVERY RECORDS ========= */
        if (requestId == null) {

            return ResponseEntity.ok(
                    jdbcTemplate.queryForList(
                            "SELECT delivery_id, request_id, delivered_by, delivered_at, quantity, status " +
                            "FROM delivery_record"));

        } else {

            return ResponseEntity.ok(
                    jdbcTemplate.queryForList(
                            "SELECT delivery_id, request_id, delivered_by, delivered_at, quantity, status " +
                            "FROM delivery_record WHERE request_id = ?",
                            requestId));
        }
    }
    
    @PostMapping("/deliveries")
    public ResponseEntity<String> createDeliveryRecord(
            @RequestHeader(value = "Role", required = false) String role,
            @RequestBody Map<String, Object> body) {

        /* ================= ROLE VALIDATION ================= */
        if (role == null || role.trim().isEmpty() ||
                (!"ADMIN".equalsIgnoreCase(role.trim())
                 && !"STORE".equalsIgnoreCase(role.trim()))) {

            throw new UnauthorizedRoleException(
                    "Only Store or Admin can create delivery records");
        }

        /* ================= INPUT VALIDATION ================= */
        Object requestIdObj = body.get("requestId");
        Object deliveredByObj = body.get("deliveredBy");
        Object qtyObj = body.get("quantity");

        if (requestIdObj == null || requestIdObj.toString().trim().isEmpty()) {
            throw new InvalidRequestException("Request ID must not be empty");
        }

        if (deliveredByObj == null || deliveredByObj.toString().trim().isEmpty()) {
            throw new InvalidRequestException("DeliveredBy must not be empty");
        }

        if (qtyObj == null || qtyObj.toString().trim().isEmpty()) {
            throw new InvalidRequestException("Quantity must not be empty");
        }

        Integer requestId;
        Integer deliveredQty;

        try {
            requestId = Integer.parseInt(requestIdObj.toString());
            deliveredQty = Integer.parseInt(qtyObj.toString());
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("Request ID and Quantity must be numbers");
        }

        if (deliveredQty <= 0) {
            throw new InvalidRequestException("Quantity must be greater than zero");
        }

        String deliveredBy = deliveredByObj.toString();

        /* ================= FETCH REQUEST ================= */
        Map<String, Object> requestRow;
        try {
            requestRow = jdbcTemplate.queryForMap(
                    "SELECT request_data, status FROM department_request WHERE request_id = ?",
                    requestId);
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidRequestException("Request ID does not exist");
        }
        /* ✅ ADD THIS SAFE CHECK HERE */
        if (requestRow.get("request_data") == null || requestRow.get("status") == null) {
            throw new InvalidRequestException("Invalid or corrupted request record");
        }
        Object rd = requestRow.get("request_data");
        Object st = requestRow.get("status");

        if (rd == null || st == null) {
            throw new InvalidRequestException("Invalid or corrupted request record");
        }

        String requestStatus = requestRow.get("status").toString();
        if (!"APPROVED".equalsIgnoreCase(requestStatus)) {
            throw new InvalidRequestException(
                    "Delivery can only be created for APPROVED requests");
        }

        /* ================= DUPLICATE DELIVERY CHECK ================= */
        Integer deliveryCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM delivery_record WHERE request_id = ?",
                Integer.class,
                requestId);

        if (deliveryCount != null && deliveryCount > 0) {
            throw new InvalidRequestException("Delivery already exists for this request");
        }

        /* ================= PARSE REQUEST JSON ================= */
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> requestData;

        try {
            requestData = mapper.readValue(
                    requestRow.get("request_data").toString(),
                    Map.class);
            
        } catch (Exception e) {
            throw new InvalidRequestException("Invalid request data format");
        }

        Integer productId = Integer.parseInt(requestData.get("productId").toString());
        Integer requestedQty = Integer.parseInt(requestData.get("quantity").toString());

        if (deliveredQty > requestedQty) {
            throw new InvalidRequestException(
                    "Delivered quantity cannot exceed requested quantity");
        }

        /* ================= STOCK VALIDATION ================= */
        Map<String, Object> productRow;
        try {
            productRow = jdbcTemplate.queryForMap(
                    "SELECT available_quantity FROM product_master WHERE product_id = ?",
                    productId);
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidRequestException("Product does not exist");
        }

        Integer availableQty = (Integer) productRow.get("available_quantity");

        if (availableQty < deliveredQty) {
            throw new InvalidRequestException("Insufficient stock for delivery");
        }

        /* ================= REDUCE STOCK ================= */
        int stockUpdated = jdbcTemplate.update(
                "UPDATE product_master " +
                "SET available_quantity = available_quantity - ? " +
                "WHERE product_id = ?",
                deliveredQty,
                productId);

        if (stockUpdated == 0) {
            throw new InvalidRequestException("Failed to update product stock");
        }

        /* ================= INSERT DELIVERY RECORD ================= */
        jdbcTemplate.update(
                "INSERT INTO delivery_record " +
                "(request_id, delivered_by, quantity, status) " +
                "VALUES (?, ?, ?, ?)",
                requestId,
                deliveredBy,
                deliveredQty,
                "DELIVERED");

        /* ================= UPDATE REQUEST STATUS ================= */
        jdbcTemplate.update(
                "UPDATE department_request SET status = 'DELIVERED' WHERE request_id = ?",
                requestId);

        return ResponseEntity.ok("Delivery record created successfully");
    }
    @PostMapping("/proof-of-receipt")
    public ResponseEntity<String> uploadProofOfReceipt(
            @RequestHeader(value = "Role", required = false) String role,
            @RequestBody Map<String, Object> body) {
        // ✅ ROLE VALIDATION
        if (role == null || role.trim().isEmpty() ||
            (!"DOCTOR".equalsIgnoreCase(role.trim())
             && !"NURSE".equalsIgnoreCase(role.trim())
             && !"DEPARTMENT_HEAD".equalsIgnoreCase(role.trim()))) {

            throw new UnauthorizedRoleException("Not authorized to upload proof");
        }
        // ✅ INPUT VALIDATION
        if (!body.containsKey("deliveryId")
                || !body.containsKey("departmentId")
                || !body.containsKey("fileUri")) {
            throw new InvalidRequestException("Required fields missing");
        }
        Integer deliveryId;
        Integer departmentId;
        String fileUri;
        try {
            deliveryId = Integer.parseInt(body.get("deliveryId").toString());
            departmentId = Integer.parseInt(body.get("departmentId").toString());
            fileUri = body.get("fileUri").toString();
        } catch (Exception e) {
            throw new InvalidRequestException("Invalid input format");
        }
        if (fileUri.trim().isEmpty()) {
            throw new InvalidRequestException("File URI cannot be empty");
        }
        // ✅ DELIVERY EXISTENCE & STATUS CHECK
        Map<String, Object> deliveryRow;
        try {
            deliveryRow = jdbcTemplate.queryForMap(
                    "SELECT status FROM delivery_record WHERE delivery_id = ?",
                    deliveryId);
        } catch (Exception e) {
            throw new InvalidRequestException("Delivery ID does not exist");
        }

        Object statusObj = deliveryRow.get("status");
        if (statusObj == null) {
            throw new InvalidRequestException("Invalid delivery record");
        }

        if (!"DELIVERED".equalsIgnoreCase(statusObj.toString())) {
            throw new InvalidRequestException("Proof allowed only for DELIVERED delivery");
        }
        // ✅ DUPLICATE PROOF CHECK
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM proof_of_receipt WHERE delivery_id = ?",
                Integer.class,
                deliveryId);

        if (count != null && count > 0) {
            throw new InvalidRequestException("Proof already uploaded");
        }

        // ✅ FINAL INSERT (NO POSSIBLE FAILURE PATH)
        jdbcTemplate.update(
                "INSERT INTO proof_of_receipt (delivery_id, department_id, file_uri, status) " +
                "VALUES (?, ?, ?, ?)",
                deliveryId, departmentId, fileUri, "RECEIVED");

        return ResponseEntity.ok("Proof of receipt uploaded successfully");
    }
    @GetMapping("/deliveries/{deliveryId}/status")
    public ResponseEntity<Map<String, Object>> viewDeliveryStatus(
            @RequestHeader(value = "Role", required = false) String role,
            @PathVariable Integer deliveryId) {

        /* ========= ROLE VALIDATION ========= */
        if (role == null || role.trim().isEmpty() ||
            (!"ADMIN".equalsIgnoreCase(role.trim())
             && !"STORE".equalsIgnoreCase(role.trim())
             && !"DOCTOR".equalsIgnoreCase(role.trim())
             && !"NURSE".equalsIgnoreCase(role.trim())
             && !"DEPARTMENT_HEAD".equalsIgnoreCase(role.trim()))) {

            throw new UnauthorizedRoleException(
                    "You are not authorized to view delivery status");
        }

        /* ========= DELIVERY FETCH ========= */
        Map<String, Object> deliveryRow;
        try {
            deliveryRow = jdbcTemplate.queryForMap(
                    "SELECT delivery_id, request_id, delivered_by, delivered_at, quantity, status " +
                    "FROM delivery_record WHERE delivery_id = ?",
                    deliveryId);
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidRequestException("Delivery ID does not exist");
        }

        /* ========= PROOF FETCH ========= */
        var proofs = jdbcTemplate.queryForList(
                "SELECT proof_id, department_id, received_at, file_uri, status " +
                "FROM proof_of_receipt WHERE delivery_id = ?",
                deliveryId);

        /* ========= FINAL RESPONSE ========= */
        Map<String, Object> response = new HashMap<>();
        response.put("delivery", deliveryRow);
        response.put("proofs", proofs);

        return ResponseEntity.ok(response);
    }
    @PutMapping("/deliveries/{deliveryId}/close")
    public ResponseEntity<String> closeRequest(
            @RequestHeader(value = "Role", required = false) String role,
            @PathVariable Integer deliveryId) {

        /* ========= ROLE VALIDATION ========= */
        if (role == null || role.trim().isEmpty() ||
            (!"ADMIN".equalsIgnoreCase(role.trim())
             && !"DEPARTMENT_HEAD".equalsIgnoreCase(role.trim()))) {

            throw new UnauthorizedRoleException(
                    "You are not authorized to close requests");
        }

        /* ========= FETCH DELIVERY ========= */
        Map<String, Object> deliveryRow;
        try {
            deliveryRow = jdbcTemplate.queryForMap(
                    "SELECT request_id, status FROM delivery_record WHERE delivery_id = ?",
                    deliveryId);
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidRequestException("Delivery ID does not exist");
        }

        if (!"DELIVERED".equalsIgnoreCase(deliveryRow.get("status").toString())) {
            throw new InvalidRequestException(
                    "Only DELIVERED requests can be closed");
        }

        Integer requestId = Integer.parseInt(deliveryRow.get("request_id").toString());

        /* ========= CHECK PROOF ========= */
        Integer proofCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM proof_of_receipt WHERE delivery_id = ?",
                Integer.class,
                deliveryId);

        if (proofCount == null || proofCount == 0) {
            throw new InvalidRequestException(
                    "Cannot close request without proof of receipt");
        }

        /* ========= CLOSE DELIVERY ========= */
        jdbcTemplate.update(
                "UPDATE delivery_record SET status = 'CLOSED' WHERE delivery_id = ?",
                deliveryId);

        /* ========= CLOSE REQUEST ========= */
        jdbcTemplate.update(
                "UPDATE department_request SET status = 'CLOSED' WHERE request_id = ?",
                requestId);

        return ResponseEntity.ok("Request closed successfully");
    }
}