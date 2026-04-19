package com.ott.platform.controller;

import com.ott.platform.dto.NotificationDTO;
import com.ott.platform.model.Notification;
import com.ott.platform.model.User;
import com.ott.platform.repository.NotificationRepository;
import com.ott.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository         userRepository;

    @Autowired
    public NotificationController(NotificationRepository notificationRepository,
                                  UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository         = userRepository;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsForUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        List<NotificationDTO> notifications = notificationRepository.findByRecipient(user).stream()
                .map(this::mapNotification)
                .toList();
        return ResponseEntity.ok(notifications);
    }

    private NotificationDTO mapNotification(Notification notification) {
        return new NotificationDTO(
                notification.getMessage(),
                notification.isRead(),
                notification.getSentAt(),
                notification.getRecipient() != null ? notification.getRecipient().getName() : null);
    }
}
