package com.danum.danum.service.chat;

import com.danum.danum.domain.chat.ChatMessage;
import com.danum.danum.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatRoomRepository chatRoomRepository;

    // Redis Topic에서 메시지 발생. 메시지 발행 후, 대기 중이던 Redis 구독서비스
    public void publish(ChannelTopic topic, ChatMessage message) {
        //Redis에 메시지 저장
        chatRoomRepository.saveChatMessage(message);

        log.info("Publishing message: " + message);
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}