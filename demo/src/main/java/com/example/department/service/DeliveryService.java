package com.example.department.service;

import java.util.List;
import java.util.Map;

import com.example.department.entity.DeliveryRecord;

public interface DeliveryService {

    void createDelivery(String role, Map<String, Object> body);

    List<DeliveryRecord> listDeliveries(String role, Integer requestId);

    void uploadProof(String role, Map<String, Object> body);

    void closeRequest(String role, Integer deliveryId);
}