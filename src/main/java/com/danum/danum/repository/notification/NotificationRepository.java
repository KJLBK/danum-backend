package com.danum.danum.repository.notification;

import com.danum.danum.domain.notification.Notification;
import com.danum.danum.domain.member.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMemberOrderByCreatedAtDesc(Member member);
    long countByMemberAndIsReadFalse(Member member);

    @Query("SELECT n FROM Notification n WHERE n.member.email = :memberEmail " +
            "AND n.roomId = :roomId AND n.type = :type AND n.isRead = false " +
            "ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByMemberEmailAndRoomIdAndType(
            @Param("memberEmail") String memberEmail,
            @Param("roomId") String roomId,
            @Param("type") Notification.NotificationType type
    );
}