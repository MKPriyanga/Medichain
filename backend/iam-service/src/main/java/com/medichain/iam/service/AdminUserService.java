package com.medichain.iam.service;
 
import com.medichain.iam.client.NotificationFeignClient;

import com.medichain.iam.dto.NotificationRequestDTO;

import com.medichain.iam.entity.Role;

import com.medichain.iam.entity.User;

import com.medichain.iam.exception.InvalidRoleException;

import com.medichain.iam.exception.UserNotFoundException;

import com.medichain.iam.repository.UserRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
 
@Service

@RequiredArgsConstructor

@Slf4j

public class AdminUserService {
 
    private final UserRepository          userRepository;

    private final AuditLogService         auditLogService;

    private final NotificationFeignClient notificationClient;
 
    @Transactional

    public void activateUser(Long userId) {

        User user = userRepository.findById(userId)

                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
 
        user.setActive(true);

        userRepository.save(user);
 
        auditLogService.log(userId, "USER_ACTIVATED", "USER",

                String.valueOf(userId), "User activated by admin");
 
        sendNotificationSafely(userId,

                "Your MediChain account has been activated. You can now login.", "IAM");
 
        log.info("User activated: id={}", userId);

    }
 
    @Transactional

    public void deactivateUser(Long userId) {

        User user = userRepository.findById(userId)

                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
 
        user.setActive(false);

        userRepository.save(user);
 
        auditLogService.log(userId, "USER_DEACTIVATED", "USER",

                String.valueOf(userId), "User deactivated by admin");
 
        sendNotificationSafely(userId,

                "Your MediChain account has been deactivated. Contact admin for support.", "IAM");
 
        log.info("User deactivated: id={}", userId);

    }
 
    @Transactional

    public void assignRole(Long userId, String roleStr) {

        // Role parsing and validation belongs in the service, not the controller

        Role role;

        try {

            role = Role.valueOf(roleStr.toUpperCase());

        } catch (IllegalArgumentException e) {

            throw new InvalidRoleException("Unknown role: " + roleStr);

        }
 
        User user = userRepository.findById(userId)

                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
 
        Role previous = user.getRole();

        user.setRole(role);

        userRepository.save(user);
 
        auditLogService.log(userId, "ROLE_ASSIGNED", "USER",

                String.valueOf(userId),

                String.format("Role changed from %s to %s", previous, role));
 
        sendNotificationSafely(userId,

                "Your role has been assigned: " + role +

                ". You can now access MediChain with your new permissions.", "IAM");
 
        log.info("Role assigned: userId={} role={}", userId, role);

    }
 
    @CircuitBreaker(name = "notification-service", fallbackMethod = "notificationFallback")

    public void sendNotificationSafely(Long userId, String message, String category) {

        notificationClient.sendNotification(

                new NotificationRequestDTO(userId, null, message, category));

        log.debug("Notification sent to userId={}", userId);

    }
 
    public void notificationFallback(Long userId, String message,

                                      String category, Throwable t) {

        log.warn("Notification failed for userId={} reason={}", userId, t.getMessage());

    }

}
