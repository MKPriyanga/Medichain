package com.example.department.service;

import java.util.Map;

public interface DepartmentRequestService {

    void createRequest(String role, Map<String, Object> request);

    void approveRequest(String role, Integer requestId);

    void rejectRequest(String role, Integer requestId);

    Object viewRequest(String role, Integer requestId);
}

