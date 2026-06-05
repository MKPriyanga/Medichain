package com.cts.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.entity.SupplierOrder;
public interface SupplierOrderRepository extends JpaRepository<SupplierOrder, Long> {
}
