package com.cts.client.delivery;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;
import com.cts.exception.InvalidRequestException;

@Component
public class DeliveryClientFallBack implements DeliveryClient {

    @Override
    public List<DeliveryRecordDTO> getAllDeliveries() {
        throw new InvalidRequestException(
            "Delivery Service is unavailable. Cannot fetch all deliveries.");
    }

    @Override
    public List<DeliveryRecordDTO> getDeliveriesByRequestId(Integer requestId) {
        throw new InvalidRequestException(
            "Delivery Service is unavailable. Cannot fetch deliveries for requestId: " + requestId);
    }

    @Override
    public DeliveryRecordDTO getDeliveryById(Integer deliveryId) {
        throw new InvalidRequestException(
            "Delivery Service is unavailable. Cannot fetch deliveryId: " + deliveryId);
    }
}