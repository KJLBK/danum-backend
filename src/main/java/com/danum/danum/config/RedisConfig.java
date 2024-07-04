package com.danum.danum.config;

import com.danum.danum.service.chat.RedisSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * redis pub/sub 메시지를 처리하는 listener 설정
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListener(RedisConnectionFactory connectionFactory,
                                                              MessageListenerAdapter listenerAdapter,
                                                              ChannelTopic topic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, topic);
        return container;
    }

    /**
     * RedisSubscriber를 처리하는 MessageListenerAdapter를 설정합니다.
     * 이 어댑터는 메시지를 수신하고 처리하기 위한 메서드를 호출합니다.
     * @param subscriber Redis에서 수신된 메시지를 처리할 subscriber입니다.
     * @return MessageListenerAdapter 객체입니다.
     */
    @Bean
    public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber);
    }

    /**
     * ChannelTopic을 설정합니다.
     * 이 주제는 메시지가 게시되고 구독될 주제를 나타냅니다.
     * @return ChannelTopic 객체입니다.
     */
    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic("chatroom");
    }

    /**
     * 어플리케이션에서 사용할 redisTemplate 설정
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        return redisTemplate;
    }

}
