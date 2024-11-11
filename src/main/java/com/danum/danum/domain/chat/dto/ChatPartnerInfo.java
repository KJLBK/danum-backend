package com.danum.danum.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatPartnerInfo {
    private final String email;
    private final String name;
    private final String profileImageUrl;

    public static ChatPartnerInfo empty() {
        return ChatPartnerInfo.builder().build();
    }
}