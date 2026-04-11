package com.example.department.service;

import com.example.department.entity.DepartmentRequest;
import com.example.department.exception.InvalidRequestException;
import com.example.department.exception.UnauthorizedRoleException;
import com.example.department.repository.DepartmentRequestRepository;
import com.example.department.repository.DepartmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class DepartmentRequestServiceImpl implements DepartmentRequestService {

    @Autowired
    private DepartmentRequestRepository repository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void createRequest(String role, Map<String, Object> request) {

        // ✅ ROLE VALIDATION
        if (role == null ||
                (!role.equalsIgnoreCase("DOCTOR")
                 && !role.equalsIgnoreCase("NURSE"))) {
            throw new UnauthorizedRoleException(
                    "Only Doctor or Nurse can create requests");
        }

        // ✅ BASIC INPUT VALIDATION
        if (!request.containsKey("departmentId")
                || !request.containsKey("products")) {
            throw new InvalidRequestException("Invalid input data");
        }

        Integer departmentId =
                Integer.parseInt(request.get("departmentId").toString());

        // ✅ DEPARTMENT MASTER VALIDATION
        boolean departmentExists =
                departmentRepository.existsById(departmentId);

        if (!departmentExists) {
            throw new InvalidRequestException("Invalid department");
        }

        Map<String, Object> products =
                (Map<String, Object>) request.get("products");

        Integer quantity =
                Integer.parseInt(products.get("quantity").toString());

        if (quantity <= 0) {
            throw new InvalidRequestException(
                    "Quantity must be greater than zero");
        }

        // ✅ DUPLICATE REQUEST CHECK
        boolean exists =
                repository.existsByDepartmentIdAndStatus(departmentId, "PENDING");

        if (exists) {
            throw new InvalidRequestException(
                    "A pending request already exists for this department");
        }

        // ✅ CONVERT PRODUCT DATA TO JSON
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(products);
        } catch (Exception e) {
            throw new InvalidRequestException("Invalid product data");
        }

        // ✅ SAVE REQUEST WITH NEW FIELDS
        DepartmentRequest dr = new DepartmentRequest();
        dr.setDepartmentId(departmentId);
        dr.setRequestData(json);
        dr.setQuantity(quantity);                    // ✅ NEW
        dr.setStatus("PENDING");
        dr.setRequestedAt(LocalDateTime.now());      // ✅ NEW

        repository.save(dr);
    }

    @Override
    public void approveRequest(String role, Integer requestId) {

        if (!role.equalsIgnoreCase("DEPARTMENT_HEAD")) {
            throw new UnauthorizedRoleException(
                    "Only Department Head can approve requests");
        }

        DepartmentRequest request =
                repository.findById(requestId)
                        .orElseThrow(() ->
                                new InvalidRequestException("Request not found"));

        request.setStatus("APPROVED");
       request.setApprovedBy(1001);

        repository.save(request);
    }

    @Override
    public void rejectRequest(String role, Integer requestId) {

        if (!role.equalsIgnoreCase("DEPARTMENT_HEAD")) {
            throw new UnauthorizedRoleException(
                    "Only Department Head can reject requests");
        }

        DepartmentRequest request =
                repository.findById(requestId)
                        .orElseThrow(() ->
                                new InvalidRequestException("Request not found"));

        request.setStatus("REJECTED");
        request.setApprovedBy(null);
        repository.save(request);
    }

    @Override
    public Object viewRequest(String role, Integer requestId) {

        if (!role.equalsIgnoreCase("DOCTOR")
                && !role.equalsIgnoreCase("NURSE")
                && !role.equalsIgnoreCase("ADMIN")
                && !role.equalsIgnoreCase("DEPARTMENT_HEAD")) {
            throw new UnauthorizedRoleException("Access denied");
        }

        return repository.findById(requestId)
                .orElseThrow(() ->
                        new InvalidRequestException("Request not found"));
    }
}