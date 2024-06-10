package com.danum.danum.controller.ai;

import com.danum.danum.domain.openai.OpenAiUserMessageDto;
import com.danum.danum.service.ai.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/open-ai")
public class OpenAiController {

	private final OpenAiService openAiService;

	@PostMapping("")
	public ResponseEntity<ChatResponse> generate(@RequestBody OpenAiUserMessageDto message) {
		return ResponseEntity.ok()
				.body(openAiService.sendMessage(message));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Void> close(@PathVariable("id") Long id) {
		openAiService.conversationClosed(id);

		return ResponseEntity.ok()
				.build();
	}

}
