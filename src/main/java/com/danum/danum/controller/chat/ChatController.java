package com.danum.danum.controller.chat;

import com.danum.danum.domain.chat.ChatMessage;
import com.danum.danum.repository.ChatRoomRepository;
import com.danum.danum.service.chat.ChatService;
import com.danum.danum.service.chat.RedisPublisher;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChatController {

    @Autowired
    private RedisPublisher redisPublisher;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat/message")
    public void message(@Payload ChatMessage message) {
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            chatRoomRepository.enterChatRoom(message.getRoomId());
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");

            // 채팅방에 입장할 때 기존 메시지들을 WebSocket으로 전송
            List<Object> previousMessages = chatRoomRepository.getMessages(message.getRoomId());
            for (Object prevMsg : previousMessages) {
                if (prevMsg instanceof ChatMessage) {
                    messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), prevMsg);
                }
            }
        }

        // 새 메시지 저장 및 발행
        if (ChatMessage.MessageType.TALK.equals(message.getType())) {
            chatRoomRepository.saveChatMessage(message);
        }

        // Websocket에 발행된 메시지를 Redis로 발행한다(publish)
        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()), message);
    }

    @GetMapping("/main/message")
    public String mainPage(Model model, Authentication authentication) {
        // 최근 대화 내역을 반환하는 로직
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            List<ChatMessage> recentMessages = chatService.getRecentMessages(email);
            model.addAttribute("최근 대화방", recentMessages);
        }
        return "main";
    }
}