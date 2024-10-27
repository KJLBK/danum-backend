package com.danum.danum.service.notification;

import com.danum.danum.domain.notification.Notification;
import com.danum.danum.domain.notification.NotificationDto;
import com.danum.danum.domain.member.Member;
import com.danum.danum.repository.notification.NotificationRepository;
import com.danum.danum.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createNotification(String memberEmail, String content, String link, Notification.NotificationType type) {
        Member member = memberRepository.findById(memberEmail)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다"));

        Notification notification = Notification.builder()
                .member(member)
                .content(content)
                .link(link)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .type(type)
                .build();

        notificationRepository.save(notification);
        log.info("Notification created: {}", notification.getId());
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getNotifications(String memberEmail) {
        Member member = memberRepository.findById(memberEmail)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다"));
        return notificationRepository.findByMemberOrderByCreatedAtDesc(member)
                .stream()
                .map(NotificationDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String memberEmail) {
        Member member = memberRepository.findById(memberEmail)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다"));
        return notificationRepository.countByMemberAndIsReadFalse(member);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));
        notification.markAsRead();
    }
}