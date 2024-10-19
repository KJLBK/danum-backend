package com.danum.danum.service.notification;

import com.danum.danum.domain.notification.Notification;
import com.danum.danum.domain.member.Member;
import com.danum.danum.repository.notification.NotificationRepository;
import com.danum.danum.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createNotification(String memberEmail, String content, String link, Notification.NotificationType type) {
        Member member = memberRepository.findById(memberEmail)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Notification notification = Notification.builder()
                .member(member)
                .content(content)
                .link(link)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .type(type)
                .build();

        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotifications(String memberEmail) {
        Member member = memberRepository.findById(memberEmail)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        return notificationRepository.findByMemberOrderByCreatedAtDesc(member);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String memberEmail) {
        Member member = memberRepository.findById(memberEmail)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        return notificationRepository.countByMemberAndIsReadFalse(member);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.markAsRead();
    }
}