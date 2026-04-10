package com.example.department.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.department.entity.ProofOfReceipt;

public interface ProofOfReceiptRepository
        extends JpaRepository<ProofOfReceipt, Integer> {

    boolean existsByDeliveryId(Integer deliveryId);

    long countByDeliveryId(Integer deliveryId);
}