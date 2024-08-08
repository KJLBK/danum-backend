package com.danum.danum.controller.chat;

import com.danum.danum.domain.chat.ChatMessage;
import com.danum.danum.repository.ChatRoomRepository;
import com.danum.danum.service.chat.RedisPublisher;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private RedisPublisher redisPublisher;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat/message")
    public void message(@Payload ChatMessage message) {
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            chatRoomRepository.enterChatRoom(message.getRoomId());
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");

            // 채팅방에 입장할 때 기존 메시지들을 WebSocket으로 전송
            List<Object> previousMessages = chatRoomRepository.getMessages(message.getRoomId());
            for (Object previousMessage : previousMessages) {
                messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), previousMessage);
            }
        } else if (ChatMessage.MessageType.TALK.equals(message.getType())) {
            // 채팅 메시지를 저장
            chatRoomRepository.saveChatMessage(message);
        }

        // Websocket에 발행된 메시지를 Redis로 발행한다(publish)
        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()), message);
    }
}
