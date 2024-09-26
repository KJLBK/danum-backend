package com.danum.danum.controller.chat;

import com.danum.danum.domain.chat.ChatMessage;
import com.danum.danum.domain.chat.ChatRoom;
import com.danum.danum.repository.ChatRoomRepository;
import com.danum.danum.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ChatRoom createRoom(@RequestBody String name, Authentication authentication) {
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

    // 채팅방 입장 및 이전 메시지 로드
    @GetMapping("/room/{roomId}/enter")
    public ResponseEntity<?> enterRoom(@PathVariable String roomId, Authentication authentication) {
        String userId = authentication.getName();
        ChatRoom chatRoom = chatRoomRepository.findRoomById(roomId);

        if (chatRoom == null) {
            return ResponseEntity.notFound().build();
        }

        if (chatRoom.isOneToOne() && !chatRoom.isValidOneToOneParticipant(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("이 1:1 채팅방에 참여할 권한이 없습니다.");
        }

        if (!chatRoom.isOneToOne() && !chatRoom.isParticipant(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("이 채팅방에 참여할 권한이 없습니다.");
        }

        List<ChatMessage> messages = chatRoomRepository.getMessages(roomId);
        chatRoomRepository.enterChatRoom(roomId);

        return ResponseEntity.ok().body(Map.of(
                "roomInfo", chatRoom,
                "messages", messages
        ));
    }

    // 최근 대화 내역 조회
    @GetMapping("/recent-messages")
    public List<ChatMessage> getRecentMessages(Authentication authentication) {
        String email = authentication.getName();
        return chatService.getRecentMessages(email);
    }

    // 채팅방 메시지 조회
    // 채팅방 메시지 조회
    @GetMapping("/room/{roomId}/messages")
    public List<ChatMessage> getRoomMessages(@PathVariable String roomId) {
        return chatRoomRepository.getMessages(roomId);
    }
}