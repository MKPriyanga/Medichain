package com.cts.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cts.client.WarehouseInventoryClient;
import com.cts.dto.ReceiveOrderDto;
import com.cts.dto.RestockCallDto;
import com.cts.entity.SupplierOrder;
import com.cts.exception.InvalidRequestException;
import com.cts.repository.SupplierOrderRepository;
import com.cts.repository.SupplierRepository;

@Service
public class SupplierOrderService {

    private static final Logger log = LoggerFactory.getLogger(SupplierOrderService.class);

    private final SupplierOrderRepository orderRepo;
    private final SupplierRepository supplierRepo;
    private final WarehouseInventoryClient warehouseClient;

    public SupplierOrderService(
            SupplierOrderRepository orderRepo,
            SupplierRepository supplierRepo,
            WarehouseInventoryClient warehouseClient) {
        this.orderRepo      = orderRepo;
        this.supplierRepo   = supplierRepo;
        this.warehouseClient = warehouseClient;
    }

    public SupplierOrder placeOrder(SupplierOrder order) {

        if (!supplierRepo.existsById(order.getSupplierId())) {
            throw new InvalidRequestException(
                    "Supplier with ID " + order.getSupplierId() + " does not exist");
        }

        if (order.getQuantity() == null || order.getQuantity() <= 0) {
            throw new InvalidRequestException("Order quantity must be >= 1");
        }

        order.setStatus("PLACED");
        return orderRepo.save(order);
    }

    public List<SupplierOrder> getAllOrders() {
        return orderRepo.findAll();
    }

    // RECEIVE ORDER — marks order as RECEIVED and restocks warehouse inventory
    public SupplierOrder receiveOrder(Long orderId, ReceiveOrderDto dto) {

        SupplierOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new InvalidRequestException("Order not found: " + orderId));

        if (!"PLACED".equals(order.getStatus())) {
            throw new InvalidRequestException(
                    "Only PLACED orders can be received. Current status: " + order.getStatus());
        }

        if (dto.getWarehouseId() == null || dto.getWarehouseId() <= 0) {
            throw new InvalidRequestException("Warehouse ID is required.");
        }

        if (dto.getQuantityReceived() == null || dto.getQuantityReceived() < 1) {
            throw new InvalidRequestException("Quantity received must be at least 1.");
        }

        // Mark order as RECEIVED
        order.setStatus("RECEIVED");
        order.setReceivedAt(LocalDateTime.now());
        SupplierOrder saved = orderRepo.save(order);

        // Restock warehouse inventory for each product in this order
        restockProducts(saved, dto);

        return saved;
    }

    private void restockProducts(SupplierOrder order, ReceiveOrderDto dto) {
        String productIdsJson = order.getProductIdsJson();
        if (productIdsJson == null || productIdsJson.isBlank()) {
            log.warn("No productIdsJson on order #{} — skipping inventory restock", order.getOrderId());
            return;
        }

        // Strip surrounding brackets if stored as "[1, 2, 3]"
        productIdsJson = productIdsJson.replaceAll("[\\[\\]\\s]", "");
        String[] parts = productIdsJson.split(",");

        for (String idStr : parts) {
            if (idStr.isBlank()) continue;
            try {
                long productId = Long.parseLong(idStr.trim());
                RestockCallDto restockDto = new RestockCallDto(
                        dto.getWarehouseId(),
                        productId,
                        dto.getQuantityReceived(),
                        order.getOrderId()
                );
                Object result = warehouseClient.restock(restockDto);
                if (result == null) {
                    log.warn("Restock fallback for productId={}, warehouseId={}", productId, dto.getWarehouseId());
                }
            } catch (NumberFormatException e) {
                log.warn("Could not parse productId '{}' from order #{}", idStr, order.getOrderId());
            } catch (Exception e) {
                log.error("Error restocking productId={}: {}", idStr, e.getMessage());
            }
        }
    }
}
