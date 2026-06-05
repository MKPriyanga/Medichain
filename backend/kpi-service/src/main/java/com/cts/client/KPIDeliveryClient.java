package com.cts.client;

import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "delivery-service", fallback = KPIDeliveryClientFallback.class)
public interface KPIDeliveryClient {

    // Get ALL raw deliveries — KPI module will do its own calculations
    @GetMapping("/api/deliveries")
    List<JsonNode> getAllDeliveries();
}