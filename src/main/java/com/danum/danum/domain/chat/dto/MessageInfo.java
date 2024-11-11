package com.danum.danum.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MessageInfo {
    private final String content;
    private final LocalDateTime timestamp;

    public static MessageInfo empty() {
        return MessageInfo.builder().build();
    }
}