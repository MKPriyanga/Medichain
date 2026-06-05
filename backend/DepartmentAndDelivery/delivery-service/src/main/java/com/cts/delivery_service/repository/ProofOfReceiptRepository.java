package com.cts.delivery_service.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cts.delivery_service.entity.ProofOfReceipt;

public interface ProofOfReceiptRepository extends JpaRepository<ProofOfReceipt, Integer> {

    Optional<ProofOfReceipt> findByDeliveryId(Integer deliveryId);
    List<ProofOfReceipt> findByDepartmentId(Integer departmentId);
}
