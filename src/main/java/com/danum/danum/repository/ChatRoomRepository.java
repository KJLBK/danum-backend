package com.danum.danum.repository;

import com.danum.danum.domain.chat.ChatMessage;
import com.danum.danum.domain.chat.ChatRoom;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ChatRoomRepository {
    List<ChatRoom> findAllRoom();
    ChatRoom findRoomById(String id);
    ChatRoom createChatRoom(String name, String userId);
    void addUserToChatRoom(String userId, String roomId);
    void removeUserFromChatRoom(String userId, String roomId);
    List<ChatRoom> findRoomsByUserId(String userId);
    void enterChatRoom(String roomId);
    ChannelTopic getTopic(String roomId);
    void saveChatMessage(ChatMessage message);
    List<ChatMessage> getMessages(String roomId);
    List<ChatMessage> getRecentMessages(String email);
    ChatRoom findOrCreateOneToOneChatRoom(String user1, String user2, String name);
    Set<String> getUsersInRoom(String roomId);
}
