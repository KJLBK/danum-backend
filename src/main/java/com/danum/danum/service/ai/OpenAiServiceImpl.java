package com.danum.danum.service.ai;

import com.danum.danum.domain.openai.OpenAiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenAiServiceImpl implements OpenAiService {

	private final OpenAiChatClient chatClient;

	public OpenAiResult sendMessage(String message) {
		return new OpenAiResult(chatClient.call(message));
	}

}
