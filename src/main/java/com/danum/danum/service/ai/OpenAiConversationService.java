package com.danum.danum.service.ai;

import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.openai.OpenAiConversation;
import org.springframework.transaction.annotation.Transactional;

public interface OpenAiConversationService {

    OpenAiConversation loadProgressingConversation(final Member member);

    @Transactional
    void conversationClosed(final Long conversationId);
}
