package com.danum.danum.service.ai;

import com.danum.danum.domain.openai.OpenAiResult;
import org.springframework.stereotype.Service;

@Service
public interface OpenAiService {

	OpenAiResult sendMessage(String message);

}
