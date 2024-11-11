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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat/message")
    public void message(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        Optional.ofNullable(headerAccessor.getUser())
                .map(Principal::getName)
                .ifPresent(sender -> {
                    log.debug("Processing message from authenticated user: {}", sender);
                    message.setSender(sender);
                    chatService.processMessage(message);
                });
    }

    @GetMapping("/main/message")
    public String mainPage(Model model, Authentication authentication) {
        Optional.ofNullable(authentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getName)
                .map(chatService::getRecentMessages)
                .ifPresent(messages -> model.addAttribute("최근 대화방", messages));

        return "main";
    }
}