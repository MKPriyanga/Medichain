package com.cts.client.delivery;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class DeliveryIntegrationService {

    private final DeliveryClient deliveryClient;

    public DeliveryIntegrationService(DeliveryClient deliveryClient) {
        this.deliveryClient = deliveryClient;
    }

    public List<DeliveryRecordDTO> getAllDeliveries() {
        return deliveryClient.getAllDeliveries();
    }

    public List<DeliveryRecordDTO> getDeliveriesByRequestId(Integer requestId) {
        return deliveryClient.getDeliveriesByRequestId(requestId);
    }

    public DeliveryRecordDTO getDeliveryById(Integer deliveryId) {
        return deliveryClient.getDeliveryById(deliveryId);
    }
}