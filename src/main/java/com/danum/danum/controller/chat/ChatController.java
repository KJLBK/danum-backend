package com.danum.danum.controller.chat;

import com.danum.danum.domain.chat.ChatRoom;
import com.danum.danum.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/createroom")
    public ChatRoom createRoom(@RequestBody String name) {
        return chatService.createRoom(name);
    }

    @GetMapping("/roomlist")
    public List<ChatRoom> findAllRoom() {
        return chatService.findAllRoom();
    }
}
