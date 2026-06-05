package com.cts.medichain.service;

import com.cts.medichain.dto.StockMovementRequestDto;
import com.cts.medichain.entity.InventoryItem;
import com.cts.medichain.entity.StockMovement;
import com.cts.medichain.entity.Warehouse;
import com.cts.medichain.repository.InventoryRepository;
import com.cts.medichain.repository.StockMovementRepository;
import com.cts.medichain.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final AuditLogService auditLogService;

    public StockMovementService(StockMovementRepository stockMovementRepository,
                                InventoryRepository inventoryRepository,
                                WarehouseRepository warehouseRepository,
                                AuditLogService auditLogService) {
        this.stockMovementRepository = stockMovementRepository;
        this.inventoryRepository = inventoryRepository;
        this.warehouseRepository = warehouseRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public StockMovement createMovement(StockMovementRequestDto request) {

        if (request.getQuantity() <= 0)
            throw new RuntimeException("Quantity must be positive");

        // Find source inventory item by itemId to get productId
        InventoryItem source = inventoryRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Inventory item not found"));

        Long productId = source.getProductId();

        // Validate warehouses exist
        Warehouse fromWarehouse = warehouseRepository.findById(request.getFromWarehouseId())
                .orElseThrow(() -> new RuntimeException("Source warehouse not found"));

        Warehouse toWarehouse = warehouseRepository.findById(request.getToWarehouseId())
                .orElseThrow(() -> new RuntimeException("Destination warehouse not found"));

        if (source.getQuantity() < request.getQuantity())
            throw new RuntimeException("Insufficient stock in source warehouse");

        // Deduct from source
        source.setQuantity(source.getQuantity() - request.getQuantity());
        source.setStatus(source.getQuantity() == 0 ? "OUT_OF_STOCK" : "AVAILABLE");
        inventoryRepository.save(source);

        // Add to destination — create if not exists
        InventoryItem destination = inventoryRepository
                .findByWarehouse_WarehouseIdAndProductId(toWarehouse.getWarehouseId(), productId)
                .orElse(null);

        if (destination == null) {
            destination = InventoryItem.builder()
                    .warehouse(toWarehouse)
                    .productId(productId)
                    .quantity(request.getQuantity())
                    .status("AVAILABLE")
                    .build();
        } else {
            destination.setQuantity(destination.getQuantity() + request.getQuantity());
            destination.setStatus("AVAILABLE");
        }
        inventoryRepository.save(destination);

        // Save movement record
        StockMovement movement = StockMovement.builder()
                .itemId(request.getItemId())
                .productId(productId)
                .fromWarehouseId(request.getFromWarehouseId())
                .toWarehouseId(request.getToWarehouseId())
                .quantity(request.getQuantity())
                .status("COMPLETED")
                .build();

        StockMovement saved = stockMovementRepository.save(movement);

        auditLogService.logAction(
                "MOVE_STOCK", "STOCK_MOVEMENT",
                saved.getMovementId(),
                "Moved productId=" + productId
                + " from warehouseId=" + fromWarehouse.getWarehouseId()
                + " to warehouseId=" + toWarehouse.getWarehouseId()
                + " quantity=" + request.getQuantity());

        return saved;
    }

    public List<StockMovement> listMovements() {
        return stockMovementRepository.findAll();
    }
}
