package com.danum.danum.service.ai;

import com.danum.danum.domain.openai.OpenAiConversation;
import com.danum.danum.domain.openai.OpenAiMessage;
import java.util.List;
import org.springframework.ai.chat.messages.MessageType;

public interface OpenAiMessageService {

    void saveMessage(final String message, final OpenAiConversation conversation, final MessageType messageType);

    List<OpenAiMessage> loadProgressingMessage(final OpenAiConversation conversation);
}
