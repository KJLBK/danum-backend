package com.danum.danum.service.ai;

import com.danum.danum.domain.openai.OpenAiConversation;
import com.danum.danum.domain.openai.OpenAiMessage;
import com.danum.danum.repository.ai.OpenAiMessageRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenAiMessageServiceImpl implements OpenAiMessageService {

    private final OpenAiMessageRepository openAiMessageRepository;

    @Override
    public void saveMessage(final String message, final OpenAiConversation conversation, final MessageType messageType) {
        LocalDateTime now = LocalDateTime.now();

        openAiMessageRepository.save(OpenAiMessage.builder()
                .createTime(now)
                .openAiConversation(conversation)
                .messageType(messageType)
                .content(message)
                .build()
        );
    }

    @Override
    public List<OpenAiMessage> loadMessageByConversation(OpenAiConversation conversation) {
        return openAiMessageRepository.findByOpenAiConversation(conversation);
    }

    @Override
    public List<OpenAiMessage> loadProgressingMessage(final OpenAiConversation conversation) {
        return openAiMessageRepository.findByOpenAiConversation(conversation);
    }

}
