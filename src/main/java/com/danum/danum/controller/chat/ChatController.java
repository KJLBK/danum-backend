package com.danum.danum.controller.chat;

import com.danum.danum.domain.chat.ChatMessage;
import com.danum.danum.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat/message")
    public void message(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        try {
            Principal user = headerAccessor.getUser();
            if (user != null) {
                String sender = user.getName();
                log.debug("Processing message from authenticated user: {}", sender);
                message.setSender(sender);
                chatService.processMessage(message);
            } else {
                log.error("No authentication found in WebSocket session");
                throw new IllegalArgumentException("User not authenticated");
            }
        } catch (IllegalArgumentException e) {
            log.error("Failed to process message: {}", e.getMessage());
        }
    }

    @GetMapping("/main/message")
    public String mainPage(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            List<ChatMessage> recentMessages = chatService.getRecentMessages(email);
            model.addAttribute("최근 대화방", recentMessages);
        }
        return "main";
    }
}