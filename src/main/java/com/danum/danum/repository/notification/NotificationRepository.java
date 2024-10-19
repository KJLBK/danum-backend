package com.danum.danum.repository.notification;

import com.danum.danum.domain.notification.Notification;
import com.danum.danum.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMemberOrderByCreatedAtDesc(Member member);
    long countByMemberAndIsReadFalse(Member member);
}