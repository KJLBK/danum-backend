package com.danum.danum.controller.ai;

import com.danum.danum.domain.openai.OpenAiResult;
import com.danum.danum.service.ai.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AiController {

	private final AiService aiService;

//	@GetMapping("/ai-test")
//	public Map aiTest() {
//		Map result = aiService.sendMessage("ai의 역사에 대해 50자 내로 설명해줘");
//		System.out.println(result);
//
//		return result;
//	}

	@GetMapping("/open-ai/generate")
	public OpenAiResult generate(String question) {
		return new OpenAiResult(question);
	}
}
