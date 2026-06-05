package com.cts.client;

import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class KPIRequestClientFallback implements KPIRequestClient {
    @Override
    public List<JsonNode> getAllRequests() {
        return Collections.emptyList();
    }
}

@Component
class KPIDeliveryClientFallback implements KPIDeliveryClient {
    @Override
    public List<JsonNode> getAllDeliveries() {
        return Collections.emptyList();
    }
}