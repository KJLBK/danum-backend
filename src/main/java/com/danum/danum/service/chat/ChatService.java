package com.danum.danum.service.chat;

import com.danum.danum.domain.chat.ChatMessage;
import com.danum.danum.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;

    /**
     * 사용자의 최근 대화 내역을 조회기능
     * @param email 사용자 이메일
     * @return 최근 대화 내역 목록
     */
    public List<ChatMessage> getRecentMessages(String email) {
        return chatRoomRepository.getRecentMessages(email);
    }
}