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
import java.util.stream.Stream;

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
        ChannelTopic existingTopic = topics.get(roomId);
        Optional.ofNullable(existingTopic)
                .map(Collections::singleton)
                .ifPresent(topic -> redisMessageListener.removeMessageListener(redisSubscriber, topic));

        ChannelTopic newTopic = new ChannelTopic(roomId);
        redisMessageListener.addMessageListener(redisSubscriber, newTopic);
        topics.put(roomId, newTopic);
        log.debug("Chat room listener registered for room: {}", roomId);
    }

    @Override
    public ChannelTopic getTopic(String roomId) {
        return topics.computeIfPresent(roomId, (key, existingTopic) -> {
            redisMessageListener.removeMessageListener(redisSubscriber, Collections.singleton(existingTopic));
            ChannelTopic newTopic = new ChannelTopic(roomId);
            redisMessageListener.addMessageListener(redisSubscriber, newTopic);
            return newTopic;
        });
    }

    @Override
    public void saveChatMessage(ChatMessage message) {
        String key = getChatMessagesKey(message.getRoomId());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));

        Optional.ofNullable(message.getTimestamp())
                .orElseGet(() -> {
                    message.setTimestamp(LocalDateTime.now());
                    return message.getTimestamp();
                });

        List<Object> existingMessages = redisTemplate.opsForList().range(key, 0, -1);
        boolean shouldSaveMessage = existingMessages.stream()
                .map(msg -> (ChatMessage) msg)
                .noneMatch(existing -> isSameMessage(existing, message));

        Optional.of(shouldSaveMessage)
                .filter(Boolean::booleanValue)
                .ifPresent(save -> {
                    redisTemplate.opsForList().rightPush(key, message);
                    log.debug("Chat message saved: {}", message);
                });
    }

    private boolean isSameMessage(ChatMessage msg1, ChatMessage msg2) {
        return Stream.of(
                msg1.getMessage().equals(msg2.getMessage()),
                msg1.getSender().equals(msg2.getSender()),
                msg1.getType() == msg2.getType(),
                Objects.equals(msg1.getTimestamp(), msg2.getTimestamp())
        ).allMatch(Boolean::booleanValue);
    }

    @Override
    public List<ChatMessage> getMessages(String roomId) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));
        return redisTemplate.opsForList().range(getChatMessagesKey(roomId), 0, -1).stream()
                .map(msg -> (ChatMessage) msg)
                .distinct()
                .sorted(Comparator.comparing(ChatMessage::getTimestamp))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> getRecentMessages(String email) {
        return getUserRooms(email).stream()
                .map(this::getMessages)
                .filter(messages -> !messages.isEmpty())
                .map(messages -> messages.get(messages.size() - 1))
                .sorted(Comparator.comparing(ChatMessage::getTimestamp).reversed())
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
        return String.join("_", CHAT_MESSAGES, roomId);
    }
}