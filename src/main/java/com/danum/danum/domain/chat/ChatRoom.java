package com.danum.danum.domain.chat;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class ChatRoom implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    private String roomId;      // 채팅방 고유 ID
    private String name;        // 채팅방 이름
    private boolean isOneToOne; // 1:1 채팅방 여부
    private Set<String> participants; // 참가자 목록

    // 일반 채팅방 생성
    public static ChatRoom create(String name) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.name = name;
        chatRoom.isOneToOne = false;
        chatRoom.participants = new HashSet<>();
        return chatRoom;
    }

    // 1:1 채팅방 생성
    public static ChatRoom createOneToOne(String name, String user1, String user2) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.name = name;
        chatRoom.isOneToOne = true;
        chatRoom.participants = new HashSet<>(Set.of(user1, user2));
        return chatRoom;
    }

    // 사용자 검증
    public boolean isParticipant(String userId) {
        return participants.contains(userId);
    }
    // 사용자가 2명 이상일시 검증
    public boolean isValidOneToOneParticipant(String userId) {
        return isOneToOne && participants.size() == 2 && isParticipant(userId);
    }
}