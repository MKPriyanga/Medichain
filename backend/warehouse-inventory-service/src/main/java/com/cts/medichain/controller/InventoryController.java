package com.cts.medichain.controller;

import com.cts.medichain.dto.DispatchRequestDto;
import com.cts.medichain.dto.InventoryRequestDto;
import com.cts.medichain.dto.RestockRequestDto;
import com.cts.medichain.entity.InventoryItem;
import com.cts.medichain.entity.Warehouse;
import com.cts.medichain.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createInventory(@RequestBody InventoryRequestDto request) {
        try {
            InventoryItem item = new InventoryItem();
            item.setProductId(request.getProductId());
            item.setQuantity(request.getQuantity());
            Warehouse warehouse = new Warehouse();
            warehouse.setWarehouseId(request.getWarehouseId());
            item.setWarehouse(warehouse);
            return ResponseEntity.ok(inventoryService.createInventory(item));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<InventoryItem>> listInventory(@RequestParam Long warehouseId) {
        return ResponseEntity.ok(inventoryService.listInventory(warehouseId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<InventoryItem>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    // ── Called by warehouse operator to top-up stock when supplier goods arrive ──
    @PostMapping("/restock")
    public ResponseEntity<?> restockInventory(@RequestBody RestockRequestDto dto) {
        try {
            return ResponseEntity.ok(inventoryService.restockInventory(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ── Called internally by delivery-service to reduce stock when goods are dispatched ──
    @PostMapping("/dispatch")
    public ResponseEntity<?> dispatchInventory(@RequestBody DispatchRequestDto dto) {
        try {
            return ResponseEntity.ok(inventoryService.dispatchInventory(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
