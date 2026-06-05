package com.cts.client.departmentRequest;

import java.util.List;
import org.springframework.stereotype.Component;

import com.cts.exception.InvalidRequestException;

@Component
public class DepartmentRequestFallBack implements DepartmentRequestClient {

    @Override
    public DepartmentRequestDTO getRequestById(Integer id) {
        throw new InvalidRequestException(
            "Department Request Service is unavailable. Cannot validate RequestId: " + id);
    }

    @Override
    public List<DepartmentRequestDTO> getAllRequests() {
        throw new InvalidRequestException(
            "Department Request Service is unavailable. Cannot fetch all requests.");
    }
}