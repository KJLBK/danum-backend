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
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisChatRoomRepository implements ChatRoomRepository {

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
        opsHashChatRoom = redisTemplate.opsForHash();
        opsHashChatRoomUsers = redisTemplate.opsForHash();
        opsHashUserChatRooms = redisTemplate.opsForHash();
        opsListChatMessages = redisTemplate.opsForList();
        topics = new HashMap<>();
    }

    @Override
    public List<ChatRoom> findAllRoom() {
        return opsHashChatRoom.values(CHAT_ROOMS);
    }

    @Override
    public ChatRoom findRoomById(String id) {
        return opsHashChatRoom.get(CHAT_ROOMS, id);
    }

    @Override
    public ChatRoom createChatRoom(String name, String userId) {
        ChatRoom chatRoom = ChatRoom.create(name);
        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        addUserToChatRoom(userId, chatRoom.getRoomId());
        return chatRoom;
    }

    @Override
    public void addUserToChatRoom(String userId, String roomId) {
        Set<String> users = getUsersInRoom(roomId);
        users.add(userId);
        opsHashChatRoomUsers.put(CHAT_ROOM_USERS, roomId, users);

        Set<String> rooms = getUserRooms(userId);
        rooms.add(roomId);
        opsHashUserChatRooms.put(USER_CHAT_ROOMS, userId, rooms);
    }

    @Override
    public void removeUserFromChatRoom(String userId, String roomId) {
        Set<String> users = getUsersInRoom(roomId);
        users.remove(userId);
        opsHashChatRoomUsers.put(CHAT_ROOM_USERS, roomId, users);

        Set<String> rooms = getUserRooms(userId);
        rooms.remove(roomId);
        opsHashUserChatRooms.put(USER_CHAT_ROOMS, userId, rooms);
    }

    @Override
    public List<ChatRoom> findRoomsByUserId(String userId) {
        Set<String> roomIds = getUserRooms(userId);
        return roomIds.stream()
                .map(this::findRoomById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void enterChatRoom(String roomId) {
        ChannelTopic topic = topics.get(roomId);
        if (topic == null) {
            topic = new ChannelTopic(roomId);
            redisMessageListener.addMessageListener(redisSubscriber, topic);
            topics.put(roomId, topic);
        }
    }

    @Override
    public ChannelTopic getTopic(String roomId) {
        return topics.computeIfAbsent(roomId, k -> {
            ChannelTopic topic = new ChannelTopic(roomId);
            redisMessageListener.addMessageListener(redisSubscriber, topic);
            return topic;
        });
    }

    @Override
    public void saveChatMessage(ChatMessage message) {
        if (message.getTimestamp() == null) {
            message.setTimestamp(LocalDateTime.now());
        }

        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));
        redisTemplate.opsForList().rightPush(getChatMessagesKey(message.getRoomId()), message);

        log.info("Saved chat message: {}", message);
    }

    @Override
    public List<ChatMessage> getMessages(String roomId) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));
        List<Object> messages = redisTemplate.opsForList().range(getChatMessagesKey(roomId), 0, -1);
        return messages.stream()
                .map(msg -> (ChatMessage) msg)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> getRecentMessages(String email) {
        Set<String> userRooms = getUserRooms(email);
        return userRooms.stream()
                .map(roomId -> {
                    List<ChatMessage> messages = getMessages(roomId);
                    if (!messages.isEmpty()) {
                        return messages.get(messages.size() - 1);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .sorted((m1, m2) -> {
                    LocalDateTime t1 = m1.getTimestamp();
                    LocalDateTime t2 = m2.getTimestamp();
                    if (t1 == null && t2 == null) return 0;
                    if (t1 == null) return 1;
                    if (t2 == null) return -1;
                    return t2.compareTo(t1);
                })
                .collect(Collectors.toList());
    }

    @Override
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

    @Override
    public Set<String> getUsersInRoom(String roomId) {
        return Optional.ofNullable(opsHashChatRoomUsers.get(CHAT_ROOM_USERS, roomId))
                .orElse(new HashSet<>());
    }

    private Set<String> getUserRooms(String userId) {
        return Optional.ofNullable(opsHashUserChatRooms.get(USER_CHAT_ROOMS, userId))
                .orElse(new HashSet<>());
    }

    private String getOneToOneChatKey(String user1, String user2) {
        List<String> users = Arrays.asList(user1, user2);
        Collections.sort(users);
        return String.join(":", users);
    }

    private String getChatMessagesKey(String roomId) {
        return CHAT_MESSAGES + "_" + roomId;
    }
}