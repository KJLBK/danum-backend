package com.danum.danum.handler;

import com.danum.danum.domain.chat.ChatRoom;
import com.danum.danum.service.chat.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final ChatService chatService;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("{}", payload);
        com.danum.danum.domain.chat.ChatMessage chatMessage = objectMapper.readValue(payload, com.danum.danum.domain.chat.ChatMessage.class);

        ChatRoom chatRoom = chatService.findRoomById(chatMessage.getRoom_Id());
        chatRoom.handlerActions(session, chatMessage, chatService);
    }
}
