package com.danum.danum.domain.notification;

import com.danum.danum.domain.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_email")
    private Member member;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private boolean isRead;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Column(name = "room_id")
    private String roomId;

    public void markAsRead() {
        this.isRead = true;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
        this.createdAt = LocalDateTime.now();  // 시간도 업데이트
    }

    public enum NotificationType {
        QUESTION_COMMENT,
        VILLAGE_COMMENT,
        CHAT_MESSAGE,
        CHAT_ROOM_INVITE
    }
}