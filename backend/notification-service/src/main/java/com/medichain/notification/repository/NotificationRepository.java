package com.medichain.notification.repository;

import com.medichain.notification.entity.Notification;
import com.medichain.notification.entity.NotificationCategory;
import com.medichain.notification.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(
            Long userId, NotificationStatus status);

    List<Notification> findByUserIdAndCategoryOrderByCreatedAtDesc(
            Long userId, NotificationCategory category);

    long countByUserIdAndStatus(Long userId, NotificationStatus status);
    @Modifying
    @Query("UPDATE Notification n SET n.status = 'READ' " +
           "WHERE n.userId = :userId AND n.status = 'UNREAD'")
    int markAllReadForUser(@Param("userId") Long userId);
}