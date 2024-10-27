package com.danum.danum.service.chat;

import com.danum.danum.domain.chat.ChatMessage;
import com.danum.danum.domain.chat.ChatRoom;
import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.notification.Notification;
import com.danum.danum.repository.ChatRoomRepository;
import com.danum.danum.repository.MemberRepository;
import com.danum.danum.repository.notification.NotificationRepository;
import com.danum.danum.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final RedisPublisher redisPublisher;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    @Override
    public List<ChatRoom> findAllRooms() {
        return chatRoomRepository.findAllRoom();
    }

    @Override
    public ChatRoom findRoomById(String roomId) {
        return chatRoomRepository.findRoomById(roomId);
    }

    @Override
    public ChatRoom createChatRoom(String name, String userId) {
        log.info("Creating new chat room: {} for user: {}", name, userId);
        return chatRoomRepository.createChatRoom(name, userId);
    }

    @Override
    public List<ChatRoom> findRoomsByUserId(String userId) {
        return chatRoomRepository.findRoomsByUserId(userId);
    }

    @Override
    public void addUserToChatRoom(String userId, String roomId) {
        log.info("Adding user: {} to room: {}", userId, roomId);
        chatRoomRepository.addUserToChatRoom(userId, roomId);
    }

    @Override
    public void removeUserFromChatRoom(String userId, String roomId) {
        log.info("Removing user: {} from room: {}", userId, roomId);
        chatRoomRepository.removeUserFromChatRoom(userId, roomId);
    }

    @Override
    public Set<String> getUsersInRoom(String roomId) {
        return chatRoomRepository.getUsersInRoom(roomId);
    }

    @Override
    public void saveChatMessage(ChatMessage message) {
        log.info("Saving chat message: {}", message);
        chatRoomRepository.saveChatMessage(message);
    }

    @Override
    public List<ChatMessage> getRoomMessages(String roomId) {
        return chatRoomRepository.getMessages(roomId);
    }

    @Override
    public List<ChatMessage> getRecentMessages(String email) {
        return chatRoomRepository.getRecentMessages(email);
    }

    @Override
    public void processMessage(ChatMessage message) {
        ChatRoom chatRoom = validateAndGetChatRoom(message.getRoomId(), message.getSender());

        handleEnterMessage(message);
        publishMessage(message);
        saveChatMessage(message);

        // ENTER 타입이 아닌 경우에만 알림 생성
        if (!ChatMessage.MessageType.ENTER.equals(message.getType())) {
            createMessageNotification(message, chatRoom);
        }
    }

    @Override
    public ChatRoom createOneToOneChatRoom(String currentUserId, String targetUserId, String name) {
        validateOneToOneChatRequest(currentUserId, targetUserId);
        log.info("Creating 1:1 chat room between users: {} and {}", currentUserId, targetUserId);

        ChatRoom chatRoom = chatRoomRepository.findOrCreateOneToOneChatRoom(currentUserId, targetUserId, name);

        // 채팅방 생성 알림
        notificationService.createNotification(
                targetUserId,
                String.format("%s님이 1:1 채팅을 시작했습니다.", currentUserId),
                "/chat/room/" + chatRoom.getRoomId(),
                Notification.NotificationType.CHAT_ROOM_INVITE
        );

        return chatRoom;
    }

    @Override
    public void enterChatRoom(String roomId) {
        log.info("User entering chat room: {}", roomId);
        chatRoomRepository.enterChatRoom(roomId);
    }

    @Override
    public ChannelTopic getTopic(String roomId) {
        return chatRoomRepository.getTopic(roomId);
    }

    private ChatRoom validateAndGetChatRoom(String roomId, String userId) {
        ChatRoom room = chatRoomRepository.findRoomById(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Chat room not found");
        }

        if (room.isOneToOne() && !room.isValidOneToOneParticipant(userId)) {
            throw new IllegalArgumentException("User not authorized for this chat room");
        }

        return room;
    }

    private void validateOneToOneChatRequest(String currentUserId, String targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new IllegalArgumentException("Cannot create chat room with yourself");
        }
    }

    private void handleEnterMessage(ChatMessage message) {
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            chatRoomRepository.enterChatRoom(message.getRoomId());
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        }
    }

    private void publishMessage(ChatMessage message) {
        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()), message);
    }

    private void createMessageNotification(ChatMessage message, ChatRoom chatRoom) {
        chatRoom.getParticipants().stream()
                .filter(participantId -> !participantId.equals(message.getSender()))
                .forEach(recipientId -> {
                    try {
                        // 읽지 않은 기존 알림들 찾기
                        List<Notification> existingNotifications =
                                notificationRepository.findUnreadByMemberEmailAndRoomIdAndType(
                                        recipientId,
                                        message.getRoomId(),
                                        Notification.NotificationType.CHAT_MESSAGE
                                );

                        if (!existingNotifications.isEmpty()) {
                            // 가장 최근 알림만 업데이트하고 나머지는 삭제
                            Notification latestNotification = existingNotifications.get(0);
                            updateExistingNotification(latestNotification, message, chatRoom);

                            // 첫 번째를 제외한 나머지 알림들 삭제
                            if (existingNotifications.size() > 1) {
                                notificationRepository.deleteAll(
                                        existingNotifications.subList(1, existingNotifications.size())
                                );
                            }
                        } else {
                            // 새 알림 생성
                            createNewChatNotification(recipientId, message, chatRoom);
                        }
                    } catch (Exception e) {
                        log.error("Failed to handle notification: {}", e.getMessage());
                    }
                });
    }

    private void updateExistingNotification(Notification notification,
                                            ChatMessage message,
                                            ChatRoom chatRoom) {
        String notificationContent = createNotificationContent(message, chatRoom);
        notification.updateContent(notificationContent);
        notificationRepository.save(notification);
    }

    private void createNewChatNotification(String recipientId,
                                           ChatMessage message,
                                           ChatRoom chatRoom) {
        Member recipient = memberRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Notification notification = Notification.builder()
                .member(recipient)
                .content(createNotificationContent(message, chatRoom))
                .link("/chat/room/" + chatRoom.getRoomId())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .roomId(chatRoom.getRoomId())  // roomId 설정
                .type(Notification.NotificationType.CHAT_MESSAGE)
                .build();

        notificationRepository.save(notification);
    }

    private String createNotificationContent(ChatMessage message, ChatRoom chatRoom) {
        if (chatRoom.isOneToOne()) {
            return String.format("%s로부터 새 메시지가 도착했습니다.", message.getSender());
        }
        return String.format("[%s] 새 메시지가 도착했습니다.", chatRoom.getName());
    }
}