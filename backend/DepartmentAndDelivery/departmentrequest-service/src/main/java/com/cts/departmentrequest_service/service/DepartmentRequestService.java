package com.cts.departmentrequest_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cts.departmentrequest_service.dto.DepartmentDto;
import com.cts.departmentrequest_service.dto.DepartmentRequestCreateDto;
import com.cts.departmentrequest_service.dto.DepartmentRequestResponseDto;
import com.cts.departmentrequest_service.dto.NotificationRequestDto;
import com.cts.departmentrequest_service.dto.ProductDto;
import com.cts.departmentrequest_service.entity.DepartmentRequest;
import com.cts.departmentrequest_service.exception.InvalidRequestException;
import com.cts.departmentrequest_service.repository.DepartmentRequestRepository;

@Service
public class DepartmentRequestService {

    private final DepartmentRequestRepository repository;
    private final DepartmentClient departmentClient;
    private final ProductClient productClient;
    private final NotificationClient notificationClient;

    public DepartmentRequestService(DepartmentRequestRepository repository,
                                    DepartmentClient departmentClient,
                                    ProductClient productClient,
                                    NotificationClient notificationClient) {
        this.repository = repository;
        this.departmentClient = departmentClient;
        this.productClient = productClient;
        this.notificationClient = notificationClient;
    }

    // CREATE REQUEST
    public DepartmentRequestResponseDto createRequest(DepartmentRequestCreateDto dto,
                                                       String createdBy,
                                                       Long createdByUserId) {

        if (dto.getQuantity() <= 0) {
            throw new InvalidRequestException("Quantity must be > 0");
        }

        if (dto.getProductIds() == null || dto.getProductIds().isEmpty()) {
            throw new InvalidRequestException("Product list cannot be empty");
        }

        // VALIDATE department exists and is ACTIVE
        DepartmentDto department = departmentClient.getDepartment(dto.getDepartmentId());
        if (department == null || "INACTIVE".equals(department.getStatus())) {
            throw new InvalidRequestException("Department not found or inactive");
        }

        // VALIDATE each productId exists and is ACTIVE
        List<ProductDto> allProducts = productClient.getAllProducts();
        if (allProducts==null || allProducts.isEmpty()) {
            throw new InvalidRequestException(
                    "Product service unavailable — cannot validate products");
        }

        List<Long> validProductIds = allProducts.stream()
                .filter(p -> "ACTIVE".equals(p.getStatus()))
                .map(ProductDto::getProductId)
                .collect(Collectors.toList());

        for (Integer productId : dto.getProductIds()) {
            if (!validProductIds.contains(productId.longValue())) {
                throw new InvalidRequestException(
                        "Product not found or inactive: " + productId);
            }
        }

        // CHECK no pending request exists for this department
        boolean exists = repository.existsByDepartmentIdAndStatus(
                dto.getDepartmentId(), "PENDING");
        if (exists) {
            throw new InvalidRequestException(
                    "Pending request already exists for department");
        }

        String productIdsJson = dto.getProductIds().toString();

        DepartmentRequest request = DepartmentRequest.builder()
                .departmentId(dto.getDepartmentId())
                .productIdsJson(productIdsJson)
                .quantity(dto.getQuantity())
                .status("PENDING")
                .createdBy(createdBy)
                .createdByUserId(createdByUserId)
                .build();

        DepartmentRequest saved = repository.save(request);

        // NOTIFY — alert department HEAD using headId
        if (department.getHeadId() != null) {
            notificationClient.sendNotification(new NotificationRequestDto(
                    department.getHeadId().longValue(),
                    saved.getRequestId().longValue(),
                    "New supply request #" + saved.getRequestId() +
                    " created by " + createdBy +
                    " and awaiting your approval.",
                    "REQUEST"
            ));
        }

        return mapToResponse(saved);
    }

    // APPROVE REQUEST
    public DepartmentRequestResponseDto approve(Integer requestId, String username) {

        DepartmentRequest request = repository.findById(requestId)
                .orElseThrow(() ->
                        new InvalidRequestException("Request not found"));

        if (!"PENDING".equals(request.getStatus())) {
            throw new InvalidRequestException("Only PENDING requests can be approved");
        }

        request.setStatus("APPROVED");
        request.setApprovedBy(username);
        DepartmentRequest saved = repository.save(request);

        // NOTIFY — alert the CREATOR (doctor/nurse) that request was approved
        if (request.getCreatedByUserId() != null) {
            notificationClient.sendNotification(new NotificationRequestDto(
                    request.getCreatedByUserId(),
                    requestId.longValue(),
                    "Your supply request #" + requestId +
                    " has been APPROVED by " + username + ".",
                    "REQUEST"
            ));
        }

        return mapToResponse(saved);
    }

    // REJECT REQUEST
    public DepartmentRequestResponseDto reject(Integer requestId, String username) {

        DepartmentRequest request = repository.findById(requestId)
                .orElseThrow(() ->
                        new InvalidRequestException("Request not found"));

        if (!"PENDING".equals(request.getStatus())) {
            throw new InvalidRequestException("Only PENDING requests can be rejected");
        }

        request.setStatus("REJECTED");
        request.setApprovedBy(username);
        DepartmentRequest saved = repository.save(request);

        // NOTIFY — alert the CREATOR (doctor/nurse) that request was rejected
        if (request.getCreatedByUserId() != null) {
            notificationClient.sendNotification(new NotificationRequestDto(
                    request.getCreatedByUserId(),
                    requestId.longValue(),
                    "Your supply request #" + requestId +
                    " has been REJECTED by " + username + ".",
                    "REQUEST"
            ));
        }

        return mapToResponse(saved);
    }

    // MARK PROCESSING — WAREHOUSE starts working on the request
    public DepartmentRequestResponseDto markProcessing(Integer requestId, String username) {

        DepartmentRequest request = repository.findById(requestId)
                .orElseThrow(() -> new InvalidRequestException("Request not found"));

        if (!"APPROVED".equals(request.getStatus())) {
            throw new InvalidRequestException(
                    "Only APPROVED requests can be marked as PROCESSING. Current: " + request.getStatus());
        }

        request.setStatus("PROCESSING");
        DepartmentRequest saved = repository.save(request);

        // NOTIFY the creator that warehouse is processing their request
        if (request.getCreatedByUserId() != null) {
            notificationClient.sendNotification(new NotificationRequestDto(
                    request.getCreatedByUserId(),
                    requestId.longValue(),
                    "Your supply request #" + requestId +
                    " is now being PROCESSED by warehouse operator " + username + ".",
                    "REQUEST"
            ));
        }

        return mapToResponse(saved);
    }

    // MARK COMPLETED — WAREHOUSE finishes the request
    public DepartmentRequestResponseDto markCompleted(Integer requestId, String username) {

        DepartmentRequest request = repository.findById(requestId)
                .orElseThrow(() -> new InvalidRequestException("Request not found"));

        if (!"PROCESSING".equals(request.getStatus())) {
            throw new InvalidRequestException(
                    "Only PROCESSING requests can be marked as COMPLETED. Current: " + request.getStatus());
        }

        request.setStatus("COMPLETED");
        DepartmentRequest saved = repository.save(request);

        // NOTIFY the creator that their request is completed
        if (request.getCreatedByUserId() != null) {
            notificationClient.sendNotification(new NotificationRequestDto(
                    request.getCreatedByUserId(),
                    requestId.longValue(),
                    "Your supply request #" + requestId +
                    " has been COMPLETED by warehouse operator " + username + ".",
                    "REQUEST"
            ));
        }

        return mapToResponse(saved);
    }

    // VIEW REQUEST
    public DepartmentRequestResponseDto view(Integer requestId) {

        DepartmentRequest request = repository.findById(requestId)
                .orElseThrow(() ->
                        new InvalidRequestException("Request not found"));

        DepartmentDto department = departmentClient.getDepartment(request.getDepartmentId());

        return new DepartmentRequestResponseDto(
                department,
                request.getRequestId(),
                request.getDepartmentId(),
                request.getProductIdsJson(),
                request.getQuantity(),
                request.getStatus(),
                request.getRequestedAt(),
                request.getApprovedBy(),
                request.getCreatedBy(),
                request.getCreatedByUserId(),
                department != null ? department.getHeadId() : null
        );
    }

    // GET ALL
    public List<DepartmentRequestResponseDto> getAllRequests() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // MAPPER
    private DepartmentRequestResponseDto mapToResponse(DepartmentRequest req) {
        return new DepartmentRequestResponseDto(
                null,
                req.getRequestId(),
                req.getDepartmentId(),
                req.getProductIdsJson(),
                req.getQuantity(),
                req.getStatus(),
                req.getRequestedAt(),
                req.getApprovedBy(),
                req.getCreatedBy(),
                req.getCreatedByUserId(),
                null
        );
    }
}
