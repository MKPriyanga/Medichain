package com.medichain.iam.service;
 
import com.medichain.iam.entity.AuditLog;

import com.medichain.iam.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;

import org.springframework.stereotype.Service;
 
import java.util.List;
 
@Service

@RequiredArgsConstructor

@Slf4j

public class AuditLogService {
 
    private final AuditLogRepository auditLogRepository;
 
    @Async

    public void log(Long userId, String action, String resourceType,

                    String resourceId, String details) {

        try {

            AuditLog entry = AuditLog.builder()

                    .userId(userId)

                    .action(action)

                    .resourceType(resourceType)

                    .resourceId(resourceId)

                    .details(details)

                    .build();

            auditLogRepository.save(entry);

            log.debug("Audit logged: userId={} action={}", userId, action);

        } catch (Exception e) {

            log.error("Failed to write audit log userId={} action={} error={}",

                    userId, action, e.getMessage());

        }

    }
 
    public List<AuditLog> getLogs(Long userId, String resourceType, String resourceId) {

        if (userId != null) {

            return auditLogRepository.findByUserIdOrderByTimestampDesc(userId);

        }

        if (resourceType != null && resourceId != null) {

            return auditLogRepository.findByResourceTypeAndResourceId(resourceType, resourceId);

        }

        return auditLogRepository.findAll();

    }

}
 