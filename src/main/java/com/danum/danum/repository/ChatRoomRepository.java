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

@Slf4j
@RequiredArgsConstructor
@Repository
public class ChatRoomRepository {

    // 채팅방(topic)에 발행되는 메시지를 처리할 리스너
    private final RedisMessageListenerContainer redisMessageListener;

    // 구독 처리 서비스
    private final RedisSubscriber redisSubscriber;

    // Redis에 저장할 키
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private static final String CHAT_MESSAGES = "CHAT_MESSAGES";

    // RedisTemplate을 통한 Redis 접근
    private final RedisTemplate<String, Object> redisTemplate;

    // Redis의 HashOperations (채팅방 저장용)
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;

    // Redis의 ListOperations (채팅 메시지 저장용)
    private ListOperations<String, Object> opsListChatMessages;

    // 채팅방의 대화 메시지를 발행하기 위한 Redis Topic 정보. 서버별로 채팅방에 매치되는 Topic 정보를 Map에 넣어 roomId로 찾을 수 있도록 함
    private Map<String, ChannelTopic> topics;

    @PostConstruct
    private void init() {
        // Redis의 HashOperations 초기화 (채팅방 정보 저장용)
        opsHashChatRoom = redisTemplate.opsForHash();

        // Redis의 ListOperations 초기화 (채팅 메시지 저장용)
        opsListChatMessages = redisTemplate.opsForList();

        // Redis Topic을 저장할 Map 초기화
        topics = new HashMap<>();
    }

    /**
     * 모든 채팅방 조회
     */
    public List<ChatRoom> findAllRoom() {
        return opsHashChatRoom.values(CHAT_ROOMS);
    }

    /**
     * roomId로 특정 채팅방 조회
     */
    public ChatRoom findRoomById(String id) {
        return opsHashChatRoom.get(CHAT_ROOMS, id);
    }

    /**
     * 채팅방 생성 : Redis Hash에 채팅방 정보 저장
     */
    public ChatRoom createChatRoom(String name) {
        // 채팅방 생성 (roomId는 UUID로 생성)
        ChatRoom chatRoom = ChatRoom.create(name);
        // 생성된 채팅방을 Redis Hash에 저장
        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    /**
     * 채팅방 입장 : Redis에 Topic을 만들고 Pub/Sub 통신을 하기 위해 리스너를 설정
     */
    public void enterChatRoom(String roomId) {
        // 기존에 있는 Topic을 가져옴
        ChannelTopic topic = topics.get(roomId);
        // 없으면 새로운 Topic 생성
        if (topic == null) {
            topic = new ChannelTopic(roomId);
            redisMessageListener.addMessageListener(redisSubscriber, topic);
            topics.put(roomId, topic);
        }
    }

    /**
     * roomId로 Redis Topic 조회 (존재하지 않으면 새로 생성)
     */
    public ChannelTopic getTopic(String roomId) {
        // Map에서 Topic 조회
        ChannelTopic topic = topics.get(roomId);
        // 없으면 새로 생성
        if (topic == null) {
            topic = new ChannelTopic(roomId);
            // RedisMessageListener에 리스너 추가
            redisMessageListener.addMessageListener(redisSubscriber, topic);
            // Map에 저장
            topics.put(roomId, topic);
        }
        return topic;
    }

    /**
     * 채팅 메시지 저장
     */
    public void saveChatMessage(ChatMessage message) {
        opsListChatMessages.rightPush(getChatMessagesKey(message.getRoomId()), message);
    }

    /**
     * 채팅방의 모든 메시지 조회
     */
    public List<Object> getMessages(String roomId) {
        return opsListChatMessages.range(getChatMessagesKey(roomId), 0, -1);
    }

    /**
     * 채팅방 메시지를 저장하는 Redis 키 생성
     */
    private String getChatMessagesKey(String roomId) {
        return CHAT_MESSAGES + "_" + roomId;
    }
}
