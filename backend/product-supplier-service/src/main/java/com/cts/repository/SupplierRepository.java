package com.cts.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.entity.Supplier;
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}