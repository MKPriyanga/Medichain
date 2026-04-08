package com.example.department.controller;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.department.exception.InvalidRequestException;
import com.example.department.exception.RequestNotFoundException;
import com.example.department.exception.UnauthorizedRoleException;

import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/department-requests")
public class DepartmentRequestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @PostMapping
    public ResponseEntity<String> createRequest(
            @RequestHeader(value = "Role", required = false) String role,
            @RequestBody Map<String, Object> request) throws Exception {

          // ROLE VALIDATION
        if (role == null || role.trim().isEmpty() ||
            (!"DOCTOR".equalsIgnoreCase(role.trim())
            && !"NURSE".equalsIgnoreCase(role.trim()))) {

            throw new UnauthorizedRoleException(
                    "Only Doctor or Nurse can create requests");
        }
          // BASIC INPUT VALIDATION
        Object deptObj = request.get("departmentId");
        if (deptObj == null || deptObj.toString().trim().isEmpty()) {
            throw new InvalidRequestException("Department ID must not be empty");
        }

        Integer departmentId;
        try {
            departmentId = Integer.parseInt(deptObj.toString());
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("Department ID must be a number");
        }

        Object productsObj = request.get("products");
        if (productsObj == null || !(productsObj instanceof Map)) {
            throw new InvalidRequestException("Product details are required");
        }

        Map<String, Object> products = (Map<String, Object>) productsObj;

        Object productIdObj = products.get("productId");
        if (productIdObj == null || productIdObj.toString().trim().isEmpty()) {
            throw new InvalidRequestException("Product ID must not be empty");
        }

        Integer productId;
        try {
            productId = Integer.parseInt(productIdObj.toString());
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("Product ID must be a number");
        }

        Object quantityObj = products.get("quantity");
        if (quantityObj == null || quantityObj.toString().trim().isEmpty()) {
            throw new InvalidRequestException("Quantity must not be empty");
        }

        Integer quantity;
        try {
            quantity = Integer.parseInt(quantityObj.toString());
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("Quantity must be a number");
        }

        if (quantity <= 0) {
            throw new InvalidRequestException("Quantity must be greater than zero");
        }

        /* =======================
           (7) DEPARTMENT VALIDATION
           ======================= */
        Integer deptCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM department_master WHERE department_id = ?",
            Integer.class, departmentId);

        if (deptCount == null || deptCount == 0) {
            throw new InvalidRequestException(
                    "Department ID " + departmentId + " does not exist");
        }
        Map<String, Object> productRow;
        try {
            productRow = jdbcTemplate.queryForMap(
                "SELECT available_quantity FROM product_master WHERE product_id = ?",
                productId
            );
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidRequestException(
                "Product ID " + productId + " does not exist");
        }

        Integer availableQty = (Integer) productRow.get("available_quantity");

        if (availableQty == null || availableQty < quantity) {
            throw new InvalidRequestException(
                    "Insufficient stock for Product ID " + productId);
        }

        /* =======================
           (5) DUPLICATE REQUEST CHECK
           ======================= */
        Integer duplicateCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM department_request " +
            "WHERE department_id = ? " +
            "AND status = 'PENDING' " +
            "AND request_data LIKE ?",
            Integer.class,
            departmentId,
            "%\"productId\":" + productId + "%");

        if (duplicateCount != null && duplicateCount > 0) {
            throw new InvalidRequestException(
                    "A pending request already exists for this product");
        }

          // SAVE REQUEST
        ObjectMapper mapper = new ObjectMapper();
        String requestDataJson = mapper.writeValueAsString(products);

        String insertSql =
            "INSERT INTO department_request (department_id, request_data, status) " +
            "VALUES (?, ?, ?)";

        jdbcTemplate.update(insertSql, departmentId, requestDataJson, "PENDING");

        return ResponseEntity.ok("Department request created successfully");
    }
    @GetMapping("/{requestId}")
    public ResponseEntity<Map<String, Object>> viewRequestStatus(
            @RequestHeader("Role") String role,
            @PathVariable Long requestId) {

        // ✅ ROLE VALIDATION
        if (!role.equalsIgnoreCase("DOCTOR")
                && !role.equalsIgnoreCase("NURSE")
                && !role.equalsIgnoreCase("DEPARTMENT_HEAD")
                && !role.equalsIgnoreCase("ADMIN")) {

            throw new UnauthorizedRoleException(
                    "You are not authorized to view request status");
        }

        if (requestId == null || requestId <= 0) {
            throw new InvalidRequestException("Invalid request ID");
        }

        String sql =
            "SELECT request_id, department_id, request_data, status " +
            "FROM department_request WHERE request_id = ?";

        try {
            Map<String, Object> dbResult =
                    jdbcTemplate.queryForMap(sql, requestId);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> requestData =
                    mapper.readValue(
                            dbResult.get("request_data").toString(),
                            Map.class);

            Map<String, Object> response = new HashMap<>();
            response.put("requestId", dbResult.get("request_id"));
            response.put("departmentId", dbResult.get("department_id"));
            response.put("status", dbResult.get("status"));
            response.put("requestData", requestData);

            return ResponseEntity.ok(response);

        } catch (EmptyResultDataAccessException e) {
            throw new InvalidRequestException(
                    "Request ID " + requestId + " does not exist");
        }
    }

    @PutMapping("/{requestId}/approve")
    public ResponseEntity<String> approveRequest(
            @RequestHeader("Role") String role,
            @PathVariable Long requestId) {

        if (!role.equalsIgnoreCase("DEPARTMENT_HEAD")) {
            return ResponseEntity.status(403)
                    .body("Only Department Head can approve requests");
        }

        String sql = "UPDATE department_request SET status = ? WHERE request_id = ?";
        jdbcTemplate.update(sql, "APPROVED", requestId);

        return ResponseEntity.ok("Request approved by Department Head");
    }
    
    @PutMapping("/{requestId}/reject")
    public ResponseEntity<String> rejectRequest(
            @RequestHeader("Role") String role,
            @PathVariable Long requestId) {

        if (!role.equalsIgnoreCase("DEPARTMENT_HEAD")) {
            return ResponseEntity.status(403)
                    .body("Only Department Head can reject requests");
        }

        String sql = "UPDATE department_request SET status = ? WHERE request_id = ?";
        jdbcTemplate.update(sql, "REJECTED", requestId);

        return ResponseEntity.ok("Request rejected by Department Head");
    }
}