package com.ott.platform.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class NotificationDTO {
    private String message;
    private boolean isRead;
    private LocalDateTime sentAt;
    private String recipientName;
}
