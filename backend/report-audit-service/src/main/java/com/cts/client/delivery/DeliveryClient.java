package com.cts.client.delivery;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "delivery-service",
    fallback = DeliveryClientFallBack.class
)
public interface DeliveryClient {

    @GetMapping("/api/deliveries")
    List<DeliveryRecordDTO> getAllDeliveries();

    @GetMapping("/api/deliveries")
    List<DeliveryRecordDTO> getDeliveriesByRequestId(
        @RequestParam("requestId") Integer requestId
    );

    @GetMapping("/api/deliveries/{deliveryId}")
    DeliveryRecordDTO getDeliveryById(
        @PathVariable("deliveryId") Integer deliveryId
    );
}