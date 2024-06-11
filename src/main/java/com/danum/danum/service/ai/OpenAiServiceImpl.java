package com.danum.danum.service.ai;

import com.danum.danum.domain.openai.OpenAiMessage;
import com.danum.danum.domain.openai.OpenAiUserMessageDto;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.OpenAiException;
import com.danum.danum.repository.OpenAiConversationRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.FunctionMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenAiServiceImpl implements OpenAiService {

    private final OpenAiConversationRepository openAiConversationRepository;

    private final ChatModel chatModel;

    @Override
    public ChatResponse sendMessage(final OpenAiUserMessageDto messageDto,
                                                 final List<OpenAiMessage> messageList) {
        // 프롬프트용 메시지 기록에 사용자 메시지 추가
        List<Message> promptMessage = convertMessageList(messageList);
        promptMessage.add(new UserMessage(messageDto.getMessage()));

        // 프롬프트 작성 및 api 응답 반환
        Prompt prompt = new Prompt(promptMessage);
        return ChatClient.create(chatModel)
                .prompt(prompt)
                .call()
                .chatResponse();
    }

    private List<Message> convertMessageList(final List<OpenAiMessage> messageList) {
        return messageList.stream()
                .map(conversation -> {
                    String content = conversation.getContent();

                    return switch (conversation.getMessageType()) {
                        case ASSISTANT -> new AssistantMessage(content);
                        case USER -> new UserMessage(content);
                        case SYSTEM -> new SystemMessage(content);
                        case FUNCTION -> new FunctionMessage(content);
                        default -> throw new OpenAiException(ErrorCode.MESSAGE_TYPE_NOT_SUPPORTED_EXCEPTION);
                    };
                })
                .collect(Collectors.toList());
    }

}
