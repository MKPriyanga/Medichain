package com.medichain.notification.controller;

import com.medichain.notification.dto.NotificationResponseDTO;
import com.medichain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService service;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getNotifications(
            @PathVariable Long userId,
            @RequestHeader("X-Auth-UserId") String tokenUserId,
            @RequestHeader("X-Auth-Role") String authRole) {
 
        if (!"ADMIN".equals(authRole) &&
                !tokenUserId.equals(String.valueOf(userId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied — you can only view your own notifications");
        }
        return ResponseEntity.ok(service.getUserNotifications(userId));
    }
 
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<?> getUnread(
            @PathVariable Long userId,
            @RequestHeader("X-Auth-UserId") String tokenUserId,
            @RequestHeader("X-Auth-Role") String authRole) {
 
        if (!"ADMIN".equals(authRole) &&
                !tokenUserId.equals(String.valueOf(userId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied — you can only view your own notifications");
        }
        return ResponseEntity.ok(service.getUnreadNotifications(userId));
    }
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @PathVariable Long userId) {
        return ResponseEntity.ok(Map.of("unreadCount", service.getUnreadCount(userId)));
    }
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String> markRead(@PathVariable Long notificationId) {
        service.markAsRead(notificationId);
        return ResponseEntity.ok("Notification marked as read");
    }
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Map<String, Integer>> markAllRead(@PathVariable Long userId) {
        int count = service.markAllAsRead(userId);
        return ResponseEntity.ok(Map.of("markedRead", count));
    }
}