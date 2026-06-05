package com.cts.medichain.service;
 
import com.cts.medichain.client.ProductClient;
import com.cts.medichain.client.ProductDTO;
import com.cts.medichain.dto.DispatchRequestDto;
import com.cts.medichain.dto.RestockRequestDto;
import com.cts.medichain.entity.InventoryItem;
import com.cts.medichain.entity.Warehouse;
import com.cts.medichain.repository.InventoryRepository;
import com.cts.medichain.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final AuditLogService auditLogService;
    private final ProductClient productClient;

    public InventoryService(InventoryRepository inventoryRepository,
                            WarehouseRepository warehouseRepository,
                            AuditLogService auditLogService,
                            ProductClient productClient) {
        this.inventoryRepository = inventoryRepository;
        this.warehouseRepository = warehouseRepository;
        this.auditLogService     = auditLogService;
        this.productClient       = productClient;
    }

    public InventoryItem createInventory(InventoryItem inventoryItem) {

        if (inventoryItem.getQuantity() <= 0)
            throw new RuntimeException("Quantity must be greater than zero");

        // Validate that the product actually exists in product-supplier-service
        ProductDTO product = productClient.getProductById(inventoryItem.getProductId());
        if (product == null) {
            throw new RuntimeException("Product with ID " + inventoryItem.getProductId() + " not found.");
        }

        Long warehouseId = inventoryItem.getWarehouse().getWarehouseId();

        Warehouse warehouse = warehouseRepository.findById(warehouseId)

                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
 
        inventoryItem.setWarehouse(warehouse);

        inventoryItem.setStatus(inventoryItem.getQuantity() > 0 ? "AVAILABLE" : "OUT_OF_STOCK");

        InventoryItem saved = inventoryRepository.save(inventoryItem);
 
        auditLogService.logAction(

                "CREATE_INVENTORY", "INVENTORY",

                saved.getItemId(),

                "Inventory created for productId=" + saved.getProductId()

                + ", warehouseId=" + warehouse.getWarehouseId()

                + ", quantity=" + saved.getQuantity());

        return saved;

    }
 
    // ── RESTOCK — called by product-supplier-service when goods arrive from supplier ──
    public InventoryItem restockInventory(RestockRequestDto dto) {

        if (dto.getQuantity() == null || dto.getQuantity() <= 0)
            throw new RuntimeException("Restock quantity must be greater than zero.");

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found: " + dto.getWarehouseId()));

        Optional<InventoryItem> existing = inventoryRepository
                .findByWarehouse_WarehouseIdAndProductId(dto.getWarehouseId(), dto.getProductId());

        InventoryItem item;
        if (existing.isPresent()) {
            // Product already in this warehouse — increment quantity
            item = existing.get();
            item.setQuantity(item.getQuantity() + dto.getQuantity());
            item.setStatus("AVAILABLE");
        } else {
            // First time this product arrives in this warehouse — create new item
            item = InventoryItem.builder()
                    .warehouse(warehouse)
                    .productId(dto.getProductId())
                    .quantity(dto.getQuantity())
                    .status("AVAILABLE")
                    .build();
        }

        InventoryItem saved = inventoryRepository.save(item);

        auditLogService.logAction(
                "RESTOCK_INVENTORY", "INVENTORY",
                saved.getItemId(),
                "Restocked productId=" + dto.getProductId()
                + ", warehouseId=" + dto.getWarehouseId()
                + ", qty=+" + dto.getQuantity()
                + (dto.getSourceOrderId() != null ? ", sourceOrderId=" + dto.getSourceOrderId() : ""));

        return saved;
    }

    // ── DISPATCH — called by delivery-service when goods are sent to a department ──
    public InventoryItem dispatchInventory(DispatchRequestDto dto) {

        if (dto.getQuantity() == null || dto.getQuantity() <= 0)
            throw new RuntimeException("Dispatch quantity must be greater than zero.");

        InventoryItem item = inventoryRepository
                .findByWarehouse_WarehouseIdAndProductId(dto.getWarehouseId(), dto.getProductId())
                .orElseThrow(() -> new RuntimeException(
                        "No inventory found for productId=" + dto.getProductId()
                        + " in warehouseId=" + dto.getWarehouseId()));

        if (item.getQuantity() < dto.getQuantity())
            throw new RuntimeException("Insufficient stock. Available: " + item.getQuantity()
                    + ", Requested: " + dto.getQuantity());

        item.setQuantity(item.getQuantity() - dto.getQuantity());
        item.setStatus(item.getQuantity() == 0 ? "OUT_OF_STOCK" : "AVAILABLE");

        InventoryItem saved = inventoryRepository.save(item);

        auditLogService.logAction(
                "DISPATCH_INVENTORY", "INVENTORY",
                saved.getItemId(),
                "Dispatched productId=" + dto.getProductId()
                + ", warehouseId=" + dto.getWarehouseId()
                + ", qty=-" + dto.getQuantity()
                + ", remaining=" + saved.getQuantity());

        return saved;
    }

    public List<InventoryItem> listInventory(Long warehouseId) {

        return inventoryRepository.findByWarehouse_WarehouseId(warehouseId);

    }
 
 
    public List<InventoryItem> getAllInventory() {

        return inventoryRepository.findAll();

    }

}
 