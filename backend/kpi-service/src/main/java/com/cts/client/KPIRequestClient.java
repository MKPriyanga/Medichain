package com.cts.client;

import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "departmentrequest-service", fallback = KPIRequestClientFallback.class)
public interface KPIRequestClient {

    // Get ALL raw department requests 
    @GetMapping("/api/department-requests")
    List<JsonNode> getAllRequests();
}