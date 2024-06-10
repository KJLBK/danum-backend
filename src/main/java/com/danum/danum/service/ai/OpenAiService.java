package com.danum.danum.service.ai;

import com.danum.danum.domain.openai.OpenAiUserMessageDto;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public interface OpenAiService {

	ChatResponse sendMessage(final OpenAiUserMessageDto messageDto);

	void conversationClosed(Long conversationId);

}
