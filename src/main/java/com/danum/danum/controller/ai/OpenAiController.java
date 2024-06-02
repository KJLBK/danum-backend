package com.danum.danum.controller.ai;

import com.danum.danum.domain.openai.OpenAiRequest;
import com.danum.danum.domain.openai.OpenAiResult;
import com.danum.danum.service.ai.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OpenAiController {

	private final OpenAiService openAiService;

//	@GetMapping("/ai-test")
//	public Map aiTest() {
//		Map result = aiService.sendMessage("ai의 역사에 대해 50자 내로 설명해줘");
//		System.out.println(result);
//
//		return result;
//	}

	@PostMapping("/open-ai/generate")
	public OpenAiResult generate(@RequestBody OpenAiRequest question) {
		return new OpenAiResult(question.getAnswer());
	}

}
