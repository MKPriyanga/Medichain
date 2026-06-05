package com.medichain.notification.service;

import com.medichain.notification.dto.NotificationRequestDTO;
import com.medichain.notification.dto.NotificationResponseDTO;
import com.medichain.notification.entity.Notification;
import com.medichain.notification.entity.NotificationCategory;
import com.medichain.notification.entity.NotificationStatus;
import com.medichain.notification.exception.InvalidCategoryException;
import com.medichain.notification.exception.NotificationNotFoundException;
import com.medichain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repository;
    @Transactional
    public void sendNotification(NotificationRequestDTO dto) {
        NotificationCategory category = parseCategory(dto.getCategory());

        Notification notification = Notification.builder()
                .userId(dto.getUserId())
                .referenceId(dto.getReferenceId())
                .message(dto.getMessage())
                .category(category)
                .status(NotificationStatus.UNREAD)
                .build();

        repository.save(notification);
        log.info("Notification created: userId={} category={}", dto.getUserId(), category);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUserNotifications(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUnreadNotifications(Long userId) {
        return repository.findByUserIdAndStatusOrderByCreatedAtDesc(
                        userId, NotificationStatus.UNREAD)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return repository.countByUserIdAndStatus(userId, NotificationStatus.UNREAD);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = repository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(
                        "Notification not found: " + notificationId));

        notification.setStatus(NotificationStatus.READ);
        repository.save(notification);
        log.debug("Notification marked read: id={}", notificationId);
    }

    @Transactional
    public int markAllAsRead(Long userId) {
        int updated = repository.markAllReadForUser(userId);
        log.info("Bulk mark-read: userId={} updated={}", userId, updated);
        return updated;
    }

    private NotificationResponseDTO toDTO(Notification n) {
        return new NotificationResponseDTO(
                n.getId(),
                n.getUserId(),
                n.getReferenceId(),
                n.getMessage(),
                n.getCategory(),
                n.getStatus(),
                n.getCreatedAt()
        );
    }

    private NotificationCategory parseCategory(String raw) {
        try {
            return NotificationCategory.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidCategoryException("Unknown notification category: " + raw);
        }
    }
}