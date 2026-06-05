package com.cts.medichain.controller;

import com.cts.medichain.dto.StockMovementRequestDto;
import com.cts.medichain.entity.StockMovement;
import com.cts.medichain.service.StockMovementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @PostMapping("/move")
    public ResponseEntity<StockMovement> createStockMovement(@RequestBody StockMovementRequestDto request) {
        return ResponseEntity.ok(stockMovementService.createMovement(request));
    }

    @GetMapping("/movements")
    public ResponseEntity<List<StockMovement>> listStockMovements() {
        return ResponseEntity.ok(stockMovementService.listMovements());
    }
}
