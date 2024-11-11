package com.danum.danum.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentChatDto {
    private String roomId;
    private String roomName;
    private boolean isOneToOne;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private String chatPartnerEmail;
    private String chatPartnerName;
    private String chatPartnerImage;

    public static class RecentChatDtoBuilder {
        private MessageInfo lastMessageInfo;
        private ChatPartnerInfo chatPartnerInfo;

        public RecentChatDtoBuilder lastMessageInfo(MessageInfo messageInfo) {
            this.lastMessage = messageInfo.getContent();
            this.lastMessageTime = messageInfo.getTimestamp();
            return this;
        }

        public RecentChatDtoBuilder chatPartnerInfo(ChatPartnerInfo partnerInfo) {
            this.chatPartnerEmail = partnerInfo.getEmail();
            this.chatPartnerName = partnerInfo.getName();
            this.chatPartnerImage = partnerInfo.getProfileImageUrl();
            return this;
        }
    }
}