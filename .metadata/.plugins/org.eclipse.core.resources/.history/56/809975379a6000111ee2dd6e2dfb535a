package com.cts.delivery_service.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.delivery_service.dto.DeliveryCreateDto;
import com.cts.delivery_service.dto.DeliveryResponseDto;
import com.cts.delivery_service.dto.DepartmentRequestDto;
import com.cts.delivery_service.dto.DispatchRequestDto;
import com.cts.delivery_service.dto.NotificationRequestDto;
import com.cts.delivery_service.entity.DeliveryRecord;
import com.cts.delivery_service.exception.InvalidRequestException;
import com.cts.delivery_service.repository.DeliveryRecordRepository;

@Service
@Transactional
public class DeliveryService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryService.class);

    private final DeliveryRecordRepository deliveryRepo;
    private final DepartmentRequestClient requestClient;
    private final NotificationClient notificationClient;
    private final WarehouseInventoryClient warehouseInventoryClient;

    public DeliveryService(DeliveryRecordRepository deliveryRepo,
                           DepartmentRequestClient requestClient,
                           NotificationClient notificationClient,
                           WarehouseInventoryClient warehouseInventoryClient) {
        this.deliveryRepo             = deliveryRepo;
        this.requestClient            = requestClient;
        this.notificationClient       = notificationClient;
        this.warehouseInventoryClient = warehouseInventoryClient;
    }

    // CREATE DELIVERY
    public DeliveryResponseDto createDelivery(DeliveryCreateDto dto, String deliveredBy) {

        if (dto.getQuantity() <= 0) {
            throw new InvalidRequestException("Quantity must be > 0");
        }

        if (dto.getWarehouseId() == null) {
            throw new InvalidRequestException("Warehouse ID is required.");
        }

        // VALIDATE request exists and is APPROVED or PROCESSING
        DepartmentRequestDto request = requestClient.getRequest(dto.getRequestId());
        if (request == null) {
            throw new InvalidRequestException(
                    "Department request not found or service unavailable");
        }
        if (!"APPROVED".equals(request.getStatus()) && !"PROCESSING".equals(request.getStatus())) {
            throw new InvalidRequestException(
                    "Only APPROVED or PROCESSING requests can have a delivery. Current status: "
                    + request.getStatus());
        }

        // VALIDATE quantity matches approved request quantity
        if (!dto.getQuantity().equals(request.getQuantity())) {
            throw new InvalidRequestException(
                    "Delivery quantity must match approved request quantity: "
                    + request.getQuantity());
        }

        // CHECK no duplicate delivery for same request
        if (deliveryRepo.existsByRequestId(dto.getRequestId())) {
            throw new InvalidRequestException(
                    "Delivery already exists for this request");
        }

        DeliveryRecord delivery = DeliveryRecord.builder()
                .requestId(dto.getRequestId())
                .quantity(dto.getQuantity())
                .deliveredBy(deliveredBy)
                .status("IN_TRANSIT")
                .warehouseId(dto.getWarehouseId())
                .build();

        DeliveryRecord saved = deliveryRepo.save(delivery);

        // REDUCE INVENTORY — dispatch each product listed in the request
        dispatchStock(request, dto.getWarehouseId(), dto.getQuantity());

        // NOTIFY — alert department HEAD using headId
        if (request.getHeadId() != null) {
            notificationClient.sendNotification(new NotificationRequestDto(
                    request.getHeadId().longValue(),
                    saved.getDeliveryId().longValue(),
                    "Your supply request #" + dto.getRequestId() +
                    " is now IN_TRANSIT, dispatched by " + deliveredBy +
                    ". Please confirm receipt when goods arrive.",
                    "DELIVERY"
            ));
        }

        return mapToResponse(saved);
    }

    /**
     * Parse productIdsJson (e.g. "1" or "1,2,3") and call dispatch for each product.
     * If warehouse service is unavailable the fallback returns null — we log and continue
     * so the delivery is still recorded.
     */
    private void dispatchStock(DepartmentRequestDto request, Long warehouseId, int totalQty) {
        if (request.getProductIdsJson() == null || request.getProductIdsJson().isBlank()) {
            log.warn("No productIdsJson on request #{} — skipping inventory dispatch", request.getRequestId());
            return;
        }

        // Strip surrounding brackets/spaces e.g. "[1, 2]" → "1,2"
        String productIdsJson = request.getProductIdsJson().replaceAll("[\\[\\]\\s]", "");
        String[] parts = productIdsJson.split(",");
        int perProduct = totalQty / parts.length; // distribute evenly
        int remainder  = totalQty % parts.length;

        for (int i = 0; i < parts.length; i++) {
            String idStr = parts[i].trim();
            if (idStr.isEmpty()) continue;
            try {
                long productId = Long.parseLong(idStr);
                int qty = perProduct + (i == 0 ? remainder : 0); // give remainder to first product
                if (qty <= 0) continue;

                DispatchRequestDto dispatchDto = new DispatchRequestDto(warehouseId, productId, qty);
                Object result = warehouseInventoryClient.dispatchInventory(dispatchDto);
                if (result == null) {
                    log.warn("Inventory dispatch fallback for productId={}, warehouseId={}, qty={}",
                            productId, warehouseId, qty);
                }
            } catch (NumberFormatException e) {
                log.warn("Could not parse productId '{}' from productIdsJson", idStr);
            } catch (Exception e) {
                log.error("Error dispatching stock for productId={}: {}", idStr, e.getMessage());
            }
        }
    }

    // LIST DELIVERIES
    public List<DeliveryResponseDto> listDeliveries(Integer requestId) {

        List<DeliveryRecord> records =
                requestId == null
                        ? deliveryRepo.findAll()
                        : deliveryRepo.findByRequestId(requestId);

        return records.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // CLOSE DELIVERY — called by DEPARTMENT_HEAD to confirm receipt
    public DeliveryResponseDto closeRequest(Integer deliveryId) {

        DeliveryRecord delivery = deliveryRepo.findById(deliveryId)
                .orElseThrow(() ->
                        new InvalidRequestException("Delivery not found"));

        if (!"IN_TRANSIT".equals(delivery.getStatus())) {
            throw new InvalidRequestException(
                    "Only IN_TRANSIT deliveries can be closed. Current status: "
                    + delivery.getStatus());
        }

        delivery.setStatus("CLOSED");
        DeliveryRecord saved = deliveryRepo.save(delivery);

        // FETCH request to get headId for notification
        DepartmentRequestDto request = requestClient.getRequest(delivery.getRequestId());

        if (request != null && request.getHeadId() != null) {
            notificationClient.sendNotification(new NotificationRequestDto(
                    request.getHeadId().longValue(),
                    deliveryId.longValue(),
                    "Delivery #" + deliveryId +
                    " for request #" + delivery.getRequestId() +
                    " has been confirmed received and CLOSED.",
                    "DELIVERY"
            ));
        }

        return mapToResponse(saved);
    }

    // GET BY ID
    public DeliveryResponseDto getDeliveryById(Integer deliveryId) {
        DeliveryRecord record = deliveryRepo.findById(deliveryId)
                .orElseThrow(() ->
                        new InvalidRequestException("Delivery not found: " + deliveryId));
        return mapToResponse(record);
    }

    // MAPPER
    private DeliveryResponseDto mapToResponse(DeliveryRecord delivery) {
        return new DeliveryResponseDto(
                delivery.getDeliveryId(),
                delivery.getRequestId(),
                delivery.getDeliveredBy(),
                delivery.getDeliveredAt(),
                delivery.getQuantity(),
                delivery.getStatus(),
                delivery.getWarehouseId()
        );
    }
}
