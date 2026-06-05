package com.cts.delivery_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.delivery_service.entity.DeliveryRecord;

public interface DeliveryRecordRepository 
	
	 extends JpaRepository<DeliveryRecord, Integer> {

		    boolean existsByRequestId(Integer requestId);
		    List<DeliveryRecord> findByRequestId(Integer requestId);

}
