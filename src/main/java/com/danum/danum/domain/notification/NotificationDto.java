package com.danum.danum.domain.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private String content;
    private String link;
    private LocalDateTime createdAt;
    private Notification.NotificationType type;
    private boolean isRead;

    public static NotificationDto from(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .link(notification.getLink())
                .createdAt(notification.getCreatedAt())
                .type(notification.getType())
                .isRead(notification.isRead())
                .build();
    }
}