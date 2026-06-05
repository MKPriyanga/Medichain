package com.cts.service;

import com.cts.dto.PurchaseRequestCreateDto;
import com.cts.entity.Product;
import com.cts.entity.PurchaseRequest;
import com.cts.entity.SupplierOrder;
import com.cts.exception.InvalidRequestException;
import com.cts.repository.ProductRepository;
import com.cts.repository.PurchaseRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchaseRequestService {

    private final PurchaseRequestRepository purchaseRequestRepo;
    private final ProductRepository productRepo;
    private final SupplierOrderService supplierOrderService;

    public PurchaseRequestService(
            PurchaseRequestRepository purchaseRequestRepo,
            ProductRepository productRepo,
            SupplierOrderService supplierOrderService) {
        this.purchaseRequestRepo = purchaseRequestRepo;
        this.productRepo = productRepo;
        this.supplierOrderService = supplierOrderService;
    }

    // ── WAREHOUSE: create ────────────────────────────────────────────────────

    public PurchaseRequest create(PurchaseRequestCreateDto dto, Long createdByUserId) {

        if (dto.getProductId() == null) {
            throw new InvalidRequestException("Product ID is required.");
        }

        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new InvalidRequestException(
                        "Product with ID " + dto.getProductId() + " does not exist."));

        if (!"ACTIVE".equals(product.getStatus())) {
            throw new InvalidRequestException(
                    "Product '" + product.getName() + "' is not active.");
        }

        if (dto.getQuantity() == null || dto.getQuantity() < 1) {
            throw new InvalidRequestException("Quantity must be at least 1.");
        }

        PurchaseRequest pr = PurchaseRequest.builder()
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .departmentRequestId(dto.getDepartmentRequestId())
                .warehouseId(dto.getWarehouseId())
                .createdByUserId(createdByUserId)
                .notes(dto.getNotes())
                .status("PENDING")
                .build();

        return purchaseRequestRepo.save(pr);
    }

    // ── GET ALL ──────────────────────────────────────────────────────────────
    // WAREHOUSE sees only their own; PROCUREMENT / ADMIN sees all

    public List<PurchaseRequest> getAll(String role, Long userId) {
        if ("WAREHOUSE".equals(role)) {
            return purchaseRequestRepo.findByCreatedByUserId(userId);
        }
        return purchaseRequestRepo.findAll();
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────

    public PurchaseRequest getById(Long id) {
        return purchaseRequestRepo.findById(id)
                .orElseThrow(() -> new InvalidRequestException(
                        "Purchase request with ID " + id + " not found."));
    }

    // ── PROCUREMENT: approve → auto-creates a SupplierOrder ──────────────────

    public SupplierOrder approve(Long purchaseRequestId, Long supplierId, Long reviewerUserId) {

        PurchaseRequest pr = getById(purchaseRequestId);

        if (!"PENDING".equals(pr.getStatus())) {
            throw new InvalidRequestException(
                    "Purchase request #" + purchaseRequestId
                    + " is not PENDING (current: " + pr.getStatus() + ").");
        }

        SupplierOrder order = SupplierOrder.builder()
                .supplierId(supplierId)
                .productIdsJson(String.valueOf(pr.getProductId()))
                .quantity(pr.getQuantity())
                .build();

        SupplierOrder savedOrder = supplierOrderService.placeOrder(order);

        pr.setStatus("ORDERED");
        pr.setOrderId(savedOrder.getOrderId());
        pr.setReviewedByUserId(reviewerUserId);
        pr.setReviewedAt(LocalDateTime.now());
        purchaseRequestRepo.save(pr);

        return savedOrder;
    }

    // ── PROCUREMENT: reject ───────────────────────────────────────────────────

    public PurchaseRequest reject(Long purchaseRequestId, Long reviewerUserId) {

        PurchaseRequest pr = getById(purchaseRequestId);

        if (!"PENDING".equals(pr.getStatus())) {
            throw new InvalidRequestException(
                    "Purchase request #" + purchaseRequestId
                    + " is not PENDING (current: " + pr.getStatus() + ").");
        }

        pr.setStatus("REJECTED");
        pr.setReviewedByUserId(reviewerUserId);
        pr.setReviewedAt(LocalDateTime.now());
        return purchaseRequestRepo.save(pr);
    }
}
