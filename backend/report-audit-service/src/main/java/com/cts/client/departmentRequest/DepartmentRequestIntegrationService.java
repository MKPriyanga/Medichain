package com.cts.client.departmentRequest;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DepartmentRequestIntegrationService {

    private final DepartmentRequestClient departmentRequestClient;

    public DepartmentRequestIntegrationService(DepartmentRequestClient departmentRequestClient) {
        this.departmentRequestClient = departmentRequestClient;
    }

    public DepartmentRequestDTO getRemoteRequestData(Integer requestId) {
        return departmentRequestClient.getRequestById(requestId);
    }

    public List<DepartmentRequestDTO> getAllRemoteRequests() {
        return departmentRequestClient.getAllRequests();
    }
}