package com.danum.danum.service.chat;

import com.danum.danum.domain.chat.ChatMessage;
import com.danum.danum.domain.chat.ChatRoom;
import com.danum.danum.domain.chat.dto.ChatPartnerInfo;
import com.danum.danum.domain.chat.dto.MessageInfo;
import com.danum.danum.domain.chat.dto.RecentChatDto;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        log.debug("Processing message - RoomId: {}, Sender: {}, Type: {}",
                message.getRoomId(), message.getSender(), message.getType());

        ChatRoom chatRoom = Optional.ofNullable(chatRoomRepository.findRoomById(message.getRoomId()))
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        Set<String> usersInRoom = chatRoomRepository.getUsersInRoom(message.getRoomId());

        Optional.of(message.getSender())
                .filter(usersInRoom::contains)
                .orElseThrow(() -> new IllegalArgumentException("User not authorized for this chat room"));

        Optional.of(message)
                .filter(msg -> msg.getType() == ChatMessage.MessageType.ENTER)
                .ifPresent(msg -> {
                    chatRoomRepository.enterChatRoom(msg.getRoomId());
                    msg.setMessage(msg.getSender() + "님이 입장하셨습니다.");
                });

        publishMessage(message);
        chatRoomRepository.saveChatMessage(message);

        Optional.of(message)
                .filter(msg -> msg.getType() != ChatMessage.MessageType.ENTER)
                .ifPresent(msg -> createMessageNotification(msg, chatRoom));
    }

    private boolean isUserAuthorizedForRoom(String userId, String roomId) {
        Set<String> usersInRoom = chatRoomRepository.getUsersInRoom(roomId);
        log.debug("Checking authorization - User: {}, Room: {}, Users in room: {}",
                userId, roomId, usersInRoom);
        return usersInRoom.contains(userId);
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
        log.debug("Entering chat room: {}", roomId);
        ChatRoom room = chatRoomRepository.findRoomById(roomId);
        log.debug("Found room: {}", room);

        chatRoomRepository.enterChatRoom(roomId);
        log.debug("Successfully entered chat room");
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

        if (room.isOneToOne()) {
            Set<String> participants = chatRoomRepository.getUsersInRoom(roomId);
            if (!participants.contains(userId)) {
                throw new IllegalArgumentException("User not authorized for this chat room");
            }
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
        Optional.of(chatRoomRepository.getTopic(message.getRoomId()))
                .ifPresent(topic -> redisPublisher.publish(topic, message));
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

    public List<RecentChatDto> getRecentChatsForUser(String userEmail, int limit) {
        return findRoomsByUserId(userEmail).stream()
                .map(room -> buildRecentChatDto(room, userEmail))
                .sorted(Comparator.comparing(RecentChatDto::getLastMessageTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private RecentChatDto buildRecentChatDto(ChatRoom room, String userEmail) {
        return RecentChatDto.builder()
                .roomId(room.getRoomId())
                .roomName(room.getName())
                .isOneToOne(room.isOneToOne())
                .lastMessageInfo(getLastMessageInfo(room.getRoomId()))
                .chatPartnerInfo(getChatPartnerInfo(room, userEmail))
                .build();
    }

    private MessageInfo getLastMessageInfo(String roomId) {
        return Optional.ofNullable(getRoomMessages(roomId))
                .filter(messages -> !messages.isEmpty())
                .map(messages -> messages.get(messages.size() - 1))
                .map(message -> MessageInfo.builder()
                        .content(message.getMessage())
                        .timestamp(message.getTimestamp())
                        .build())
                .orElse(MessageInfo.empty());
    }

    private ChatPartnerInfo getChatPartnerInfo(ChatRoom room, String userEmail) {
        return Optional.of(room)
                .filter(ChatRoom::isOneToOne)
                .map(r -> r.getParticipants().stream()
                        .filter(participantId -> !participantId.equals(userEmail))
                        .findFirst()
                        .flatMap(memberRepository::findById)
                        .map(partner -> ChatPartnerInfo.builder()
                                .email(partner.getEmail())
                                .name(partner.getName())
                                .profileImageUrl(Optional.ofNullable(partner.getProfileImageUrl())
                                        .filter(url -> !url.isEmpty())
                                        .orElse(null))
                                .build())
                        .orElse(ChatPartnerInfo.empty()))
                .orElse(ChatPartnerInfo.empty());
    }
}