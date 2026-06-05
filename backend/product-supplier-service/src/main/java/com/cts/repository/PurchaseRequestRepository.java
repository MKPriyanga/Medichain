package com.cts.repository;

import com.cts.entity.PurchaseRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {
    List<PurchaseRequest> findByCreatedByUserId(Long createdByUserId);
    List<PurchaseRequest> findByStatus(String status);
}
