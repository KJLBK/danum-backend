package com.danum.danum.service.ai;

import com.danum.danum.domain.openai.OpenAiMessage;
import com.danum.danum.domain.openai.OpenAiUserMessageDto;
import java.util.List;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public interface OpenAiService {

    ChatResponse sendMessage(final OpenAiUserMessageDto messageDto, final List<OpenAiMessage> messageList);

}
