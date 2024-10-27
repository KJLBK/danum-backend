package com.danum.danum.controller.chat;

import com.danum.danum.domain.chat.ChatMessage;
import com.danum.danum.domain.chat.ChatRoom;
import com.danum.danum.repository.ChatRoomRepository;
import com.danum.danum.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatService chatService;

    @GetMapping("/room")
    public String rooms() {
        return "/chat/room";
    }

    @GetMapping("/rooms")
    public List<ChatRoom> rooms(Authentication authentication) {
        return chatService.findRoomsByUserId(authentication.getName());
    }

    @PostMapping("/room")
    public ChatRoom createRoom(@RequestBody String name, Authentication authentication) {
        return chatService.createChatRoom(name, authentication.getName());
    }

    @GetMapping("/room/{roomId}")
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return chatService.findRoomById(roomId);
    }

    @PostMapping("/room/one-to-one")
    public ResponseEntity<?> createOneToOneChat(
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        String currentUserId = authentication.getName();
        String targetUserId = payload.get("targetUserId");

        if (targetUserId == null || targetUserId.isEmpty()) {
            return ResponseEntity.badRequest().body("targetUserId is required");
        }

        try {
            ChatRoom chatRoom = chatService.createOneToOneChatRoom(currentUserId, targetUserId, "1:1 채팅");
            return ResponseEntity.ok().body(Map.of("roomId", chatRoom.getRoomId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/room/{roomId}/enter")
    public ResponseEntity<?> enterRoom(@PathVariable String roomId, Authentication authentication) {
        try {
            String userId = authentication.getName();
            ChatRoom chatRoom = chatService.findRoomById(roomId);

            if (chatRoom == null) {
                return ResponseEntity.notFound().build();
            }

            chatService.enterChatRoom(roomId);
            List<ChatMessage> messages = chatService.getRoomMessages(roomId);

            return ResponseEntity.ok().body(Map.of(
                    "roomInfo", chatRoom,
                    "messages", messages
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/recent-messages")
    public List<ChatMessage> getRecentMessages(Authentication authentication) {
        return chatService.getRecentMessages(authentication.getName());
    }

    @GetMapping("/room/{roomId}/messages")
    public List<ChatMessage> getRoomMessages(@PathVariable String roomId) {
        return chatService.getRoomMessages(roomId);
    }
}