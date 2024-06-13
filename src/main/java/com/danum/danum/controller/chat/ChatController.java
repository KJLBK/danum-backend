package com.danum.danum.controller.chat;

import com.danum.danum.domain.chat.ChatMessage;
import com.danum.danum.repository.ChatRoomRepository;
import com.danum.danum.service.chat.RedisPublisher;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class ChatController {

    @Autowired
    private RedisPublisher redisPublisher;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @MessageMapping("/chat/message")
    public void message(@Payload ChatMessage message) {
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            chatRoomRepository.enterChatRoom(message.getRoomId());
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        }
        // Websocket에 발행된 메시지를 redis로 발행한다(publish)
        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()), message);
    }
}
