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
    private static final String ONE_TO_ONE_CHAT_ROOMS = "ONE_TO_ONE_CHAT_ROOMS";

    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    private HashOperations<String, String, Set<String>> opsHashChatRoomUsers;
    private HashOperations<String, String, Set<String>> opsHashUserChatRooms;
    private ListOperations<String, Object> opsListChatMessages;

    private Map<String, ChannelTopic> topics;

    @PostConstruct
    private void init() {
        // Redis operations 초기화
        opsHashChatRoom = redisTemplate.opsForHash();
        opsHashChatRoomUsers = redisTemplate.opsForHash();
        opsHashUserChatRooms = redisTemplate.opsForHash();
        opsListChatMessages = redisTemplate.opsForList();
        topics = new HashMap<>();
    }

    //모든 채팅방 조회
    public List<ChatRoom> findAllRoom() {
        return opsHashChatRoom.values(CHAT_ROOMS);
    }

    // ID로 채팅방 조회
    public ChatRoom findRoomById(String id) {
        return opsHashChatRoom.get(CHAT_ROOMS, id);
    }

    //새 채팅방 생성
    public ChatRoom createChatRoom(String name, String userId) {
        ChatRoom chatRoom = ChatRoom.create(name);
        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        addUserToChatRoom(userId, chatRoom.getRoomId());
        return chatRoom;
    }

    // 채팅방에 사용자 추가
    public void addUserToChatRoom(String userId, String roomId) {
        // 채팅방의 사용자 목록에 사용자 추가
        Set<String> users = opsHashChatRoomUsers.get(CHAT_ROOM_USERS, roomId);
        if (users == null) {
            users = new HashSet<>();
        }
        users.add(userId);
        opsHashChatRoomUsers.put(CHAT_ROOM_USERS, roomId, users);

        // 사용자의 채팅방 목록에 채팅방 추가
        Set<String> rooms = opsHashUserChatRooms.get(USER_CHAT_ROOMS, userId);
        if (rooms == null) {
            rooms = new HashSet<>();
        }
        rooms.add(roomId);
        opsHashUserChatRooms.put(USER_CHAT_ROOMS, userId, rooms);
    }

    // 채팅방에서 사용자 제거
    public void removeUserFromChatRoom(String userId, String roomId) {
        Set<String> users = opsHashChatRoomUsers.get(CHAT_ROOM_USERS, roomId);
        if (users != null) {
            users.remove(userId);
            opsHashChatRoomUsers.put(CHAT_ROOM_USERS, roomId, users);
        }

        Set<String> rooms = opsHashUserChatRooms.get(USER_CHAT_ROOMS, userId);
        if (rooms != null) {
            rooms.remove(roomId);
            opsHashUserChatRooms.put(USER_CHAT_ROOMS, userId, rooms);
        }
    }

    // 사용자가 참여한 모든 채팅방 조회
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

    // 채팅방 입장 처리
    public void enterChatRoom(String roomId) {
        ChannelTopic topic = topics.get(roomId);
        if (topic == null) {
            topic = new ChannelTopic(roomId);
            redisMessageListener.addMessageListener(redisSubscriber, topic);
            topics.put(roomId, topic);
        }
    }

    // 채팅방의 Topic 가져오기 (없으면 생성)
    public ChannelTopic getTopic(String roomId) {
        return topics.computeIfAbsent(roomId, k -> {
            ChannelTopic topic = new ChannelTopic(roomId);
            redisMessageListener.addMessageListener(redisSubscriber, topic);
            return topic;
        });
    }

    // 채팅 메시지 저장
    public void saveChatMessage(ChatMessage message) {
        opsListChatMessages.rightPush(getChatMessagesKey(message.getRoomId()), message);
    }

    // 채팅방의 모든 메시지 조회
    public List<Object> getMessages(String roomId) {
        return opsListChatMessages.range(getChatMessagesKey(roomId), 0, -1);
    }

    // 채팅 메시지 키 생성
    private String getChatMessagesKey(String roomId) {
        return CHAT_MESSAGES + "_" + roomId;
    }

    // 사용자의 최근 메시지 조회
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

    // 1:1 채팅방 찾기 또는 생성
    public ChatRoom findOrCreateOneToOneChatRoom(String user1, String user2, String name) {
        String key = getOneToOneChatKey(user1, user2);
        String roomId = (String) redisTemplate.opsForHash().get(ONE_TO_ONE_CHAT_ROOMS, key);

        if (roomId != null) {
            return findRoomById(roomId);
        }

        ChatRoom chatRoom = ChatRoom.createOneToOne(name, user1, user2);
        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        redisTemplate.opsForHash().put(ONE_TO_ONE_CHAT_ROOMS, key, chatRoom.getRoomId());

        addUserToChatRoom(user1, chatRoom.getRoomId());
        addUserToChatRoom(user2, chatRoom.getRoomId());

        return chatRoom;
    }

    // 1:1 채팅방 키 생성
    private String getOneToOneChatKey(String user1, String user2) {
        return user1.compareTo(user2) < 0 ? user1 + ":" + user2 : user2 + ":" + user1;
    }
}