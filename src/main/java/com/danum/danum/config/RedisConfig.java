package com.danum.danum.config;

import com.danum.danum.service.chat.RedisSubscriber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

//    @Value("${spring.redis.password}")
//    private String redisPassword;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Redis 단독 서버 설정을 생성후, 호스트,포트,비밀번호 설정
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
//        config.setPassword(redisPassword);
        return new LettuceConnectionFactory(config);
    }

    // 메시지 리스너 컨테이너 설정하는 빈
    @Bean
    public RedisMessageListenerContainer redisMessageListener(RedisConnectionFactory connectionFactory,
                                                              MessageListenerAdapter listenerAdapter,
                                                              ChannelTopic topic) {
        // 레디스 메시지리스너 컨테이너 생성후 연결팩토리, 메시지 리스너, 토픽 설정부분
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, topic);
        return container;
    }

    //메시지 리스너 어댑터 설정
    @Bean
    public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber) {
        // RedisSubscriber를 메시지 리스너로 사용하는 어댑터 반환
        return new MessageListenerAdapter(subscriber);
    }

    @Bean
    public ChannelTopic topic() {
        // Redis 채널 토픽 설정
        // chatroom 이라는 이름의 토픽 생성 및 반환
        return new ChannelTopic("chatroom");
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        // RedisTemplate 객체를 생성하고 연결 팩토리를 설정
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        // 키는 문자열로 직렬화, 값은 JSON 형태의 문자열로 직렬화
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        return redisTemplate;
    }

    // 레디스 탬플릿 설정
}
