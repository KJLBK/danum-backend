package com.danum.danum.domain.openai;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OpenAiResponse {

    private OpenAiConversation conversation;

    private String message;

}
