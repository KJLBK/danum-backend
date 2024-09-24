package com.danum.danum.repository;

import com.danum.danum.domain.chat.ChatMessage;
import com.danum.danum.domain.chat.ChatRoom;
import com.danum.danum.service.chat.RedisSubscriber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ChatRoomRepository {

    private final RedisMessageListenerContainer redisMessageListener;
    private final RedisSubscriber redisSubscriber;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private static final String CHAT_ROOM_USERS = "CHAT_ROOM_USERS";
    private static final String USER_CHAT_ROOMS = "USER_CHAT_ROOMS";
    private static final String CHAT_MESSAGES = "CHAT_MESSAGES";

    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    private HashOperations<String, String, Set<String>> opsHashChatRoomUsers;
    private HashOperations<String, String, Set<String>> opsHashUserChatRooms;
    private ListOperations<String, Object> opsListChatMessages;

    private Map<String, ChannelTopic> topics;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
        opsHashChatRoomUsers = redisTemplate.opsForHash();
        opsHashUserChatRooms = redisTemplate.opsForHash();
        opsListChatMessages = redisTemplate.opsForList();
        topics = new HashMap<>();
    }

    public List<ChatRoom> findAllRoom() {
        return opsHashChatRoom.values(CHAT_ROOMS);
    }

    public ChatRoom findRoomById(String id) {
        return opsHashChatRoom.get(CHAT_ROOMS, id);
    }

    public ChatRoom createChatRoom(String name, String userId) {
        ChatRoom chatRoom = ChatRoom.create(name);
        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        addUserToChatRoom(userId, chatRoom.getRoomId());
        return chatRoom;
    }

    public void addUserToChatRoom(String userId, String roomId) {
        // 채팅방에 사용자 추가
        Set<String> users = opsHashChatRoomUsers.get(CHAT_ROOM_USERS, roomId);
        if (users == null) {
            users = new HashSet<>();
        }
        users.add(userId);
        opsHashChatRoomUsers.put(CHAT_ROOM_USERS, roomId, users);

        // 사용자의 채팅방 목록에 추가
        Set<String> rooms = opsHashUserChatRooms.get(USER_CHAT_ROOMS, userId);
        if (rooms == null) {
            rooms = new HashSet<>();
        }
        rooms.add(roomId);
        opsHashUserChatRooms.put(USER_CHAT_ROOMS, userId, rooms);
    }

    public void removeUserFromChatRoom(String userId, String roomId) {
        // 채팅방에서 사용자 제거
        Set<String> users = opsHashChatRoomUsers.get(CHAT_ROOM_USERS, roomId);
        if (users != null) {
            users.remove(userId);
            opsHashChatRoomUsers.put(CHAT_ROOM_USERS, roomId, users);
        }

        // 사용자의 채팅방 목록에서 제거
        Set<String> rooms = opsHashUserChatRooms.get(USER_CHAT_ROOMS, userId);
        if (rooms != null) {
            rooms.remove(roomId);
            opsHashUserChatRooms.put(USER_CHAT_ROOMS, userId, rooms);
        }
    }

    public List<ChatRoom> findRoomsByUserId(String userId) {
        Set<String> roomIds = opsHashUserChatRooms.get(USER_CHAT_ROOMS, userId);
        if (roomIds == null) {
            return new ArrayList<>();
        }
        return roomIds.stream()
                .map(this::findRoomById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void enterChatRoom(String roomId) {
        ChannelTopic topic = topics.get(roomId);
        if (topic == null) {
            topic = new ChannelTopic(roomId);
            redisMessageListener.addMessageListener(redisSubscriber, topic);
            topics.put(roomId, topic);
        }
    }

    public ChannelTopic getTopic(String roomId) {
        return topics.computeIfAbsent(roomId, k -> {
            ChannelTopic topic = new ChannelTopic(roomId);
            redisMessageListener.addMessageListener(redisSubscriber, topic);
            return topic;
        });
    }

    public void saveChatMessage(ChatMessage message) {
        opsListChatMessages.rightPush(getChatMessagesKey(message.getRoomId()), message);
    }

    public List<Object> getMessages(String roomId) {
        return opsListChatMessages.range(getChatMessagesKey(roomId), 0, -1);
    }

    private String getChatMessagesKey(String roomId) {
        return CHAT_MESSAGES + "_" + roomId;
    }

    public List<ChatMessage> getRecentMessages(String email) {
        Set<String> userRooms = opsHashUserChatRooms.get(USER_CHAT_ROOMS, email);
        if (userRooms == null) {
            return new ArrayList<>();
        }
        return userRooms.stream()
                .map(roomId -> {
                    List<Object> messages = getMessages(roomId);
                    if (!messages.isEmpty()) {
                        return (ChatMessage) messages.get(messages.size() - 1);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(ChatMessage::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
}