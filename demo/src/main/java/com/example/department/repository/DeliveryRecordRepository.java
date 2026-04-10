package com.example.department.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.department.entity.DeliveryRecord;

import java.util.List;

public interface DeliveryRecordRepository
        extends JpaRepository<DeliveryRecord, Integer> {

    boolean existsByRequestId(Integer requestId);

    List<DeliveryRecord> findByRequestId(Integer requestId);
}