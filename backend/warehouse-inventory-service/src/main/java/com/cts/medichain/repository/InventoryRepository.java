package com.cts.medichain.repository;

import com.cts.medichain.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {

    List<InventoryItem> findByWarehouse_WarehouseId(Long warehouseId);

    Optional<InventoryItem> findByWarehouse_WarehouseIdAndProductId(
            Long warehouseId, Long productId);
}