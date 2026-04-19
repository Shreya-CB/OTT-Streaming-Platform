package com.ott.platform.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Notification sent to a User by NotificationService (Pure Fabrication).
 */
@Entity
@Table(name = "notifications")
@Getter @Setter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;

    private boolean isRead = false;

    private LocalDateTime sentAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User recipient;

    public Notification(String message, User recipient) {
        this.message   = message;
        this.recipient = recipient;
        this.sentAt    = LocalDateTime.now();
        this.isRead    = false;
    }
}
