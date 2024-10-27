package com.danum.danum.service.chat;

import com.danum.danum.domain.chat.ChatMessage;
import com.danum.danum.domain.chat.ChatRoom;
import org.springframework.data.redis.listener.ChannelTopic;
import java.util.List;
import java.util.Set;

public interface ChatService {
    // 채팅방 관련
    List<ChatRoom> findAllRooms();
    ChatRoom findRoomById(String roomId);
    ChatRoom createChatRoom(String name, String userId);
    List<ChatRoom> findRoomsByUserId(String userId);
    void addUserToChatRoom(String userId, String roomId);
    void removeUserFromChatRoom(String userId, String roomId);
    Set<String> getUsersInRoom(String roomId);

    // 메시지 관련
    void saveChatMessage(ChatMessage message);
    List<ChatMessage> getRoomMessages(String roomId);
    List<ChatMessage> getRecentMessages(String email);
    void processMessage(ChatMessage message);

    // 1:1 채팅
    ChatRoom createOneToOneChatRoom(String currentUserId, String targetUserId, String name);

    // 채팅방 참여
    void enterChatRoom(String roomId);
    ChannelTopic getTopic(String roomId);
}