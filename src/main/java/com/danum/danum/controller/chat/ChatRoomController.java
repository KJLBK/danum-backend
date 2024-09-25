package com.danum.danum.controller.chat;

import com.danum.danum.domain.chat.ChatMessage;
import com.danum.danum.domain.chat.ChatRoom;
import com.danum.danum.repository.ChatRoomRepository;
import com.danum.danum.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    // 채팅방 목록 화면
    @GetMapping("/room")
    public String rooms() {
        return "/chat/room";
    }

    // 사용자의 모든 채팅방 목록 조회
    @GetMapping("/rooms")
    public List<ChatRoom> rooms(Authentication authentication) {
        String userId = authentication.getName();
        return chatRoomRepository.findRoomsByUserId(userId);
    }

    // 새로운 채팅방 생성
    @PostMapping("/room")
    public ChatRoom createRoom(@RequestParam String name, Authentication authentication) {
        String userId = authentication.getName();
        return chatRoomRepository.createChatRoom(name, userId);
    }

    // 특정 채팅방 정보 조회
    @GetMapping("/room/{roomId}")
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return chatRoomRepository.findRoomById(roomId);
    }

    // 1:1 채팅방 생성
    @PostMapping("/room/one-to-one")
    public ResponseEntity<?> createOneToOneChat(
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        String currentUserId = authentication.getName();
        String targetUserId = payload.get("targetUserId");

        if (targetUserId == null || targetUserId.isEmpty()) {
            return ResponseEntity.badRequest().body("targetUserId is required");
        }

        if (currentUserId.equals(targetUserId)) {
            return ResponseEntity.badRequest().body("자신과 채팅을 시작할 수 없습니다");
        }

        ChatRoom chatRoom = chatRoomRepository.findOrCreateOneToOneChatRoom(currentUserId, targetUserId, "1:1 채팅");

        return ResponseEntity.ok().body(Map.of("roomId", chatRoom.getRoomId()));
    }

    // 특정 채팅방의 모든 메시지 조회
    @GetMapping("/room/{roomId}/messages")
    public List<Object> getRoomMessages(@PathVariable String roomId) {
        return chatRoomRepository.getMessages(roomId);
    }

    // 사용자의 최근 대화 내역 조회
    @GetMapping("/recent-messages")
    public List<ChatMessage> getRecentMessages(Authentication authentication) {
        String email = authentication.getName();
        return chatService.getRecentMessages(email);
    }
}