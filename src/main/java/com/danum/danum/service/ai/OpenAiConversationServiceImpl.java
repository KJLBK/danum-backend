package com.danum.danum.service.ai;

import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.openai.OpenAiConversation;
import com.danum.danum.domain.openai.OpenAiConversationStatus;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.custom.OpenAiException;
import com.danum.danum.repository.ai.OpenAiConversationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OpenAiConversationServiceImpl implements OpenAiConversationService {

    private final OpenAiConversationRepository openAiConversationRepository;

    @Override
    public OpenAiConversation loadConversation(final Long id) {
        return openAiConversationRepository.findById(id)
                .orElseThrow(() -> new OpenAiException(ErrorCode.NO_SUCH_CONVERSATION_EXCEPTION));
    }

    @Override
    public OpenAiConversation loadProgressingConversation(final Member member) {
        List<OpenAiConversation> conversationList = openAiConversationRepository.findByMemberAndStatus(member,
                OpenAiConversationStatus.PROCESSING);

        if (conversationList.isEmpty()) {
            return generateConversation(member);
        }

        if (conversationList.size() > 1) {
            throw new OpenAiException(ErrorCode.MULTIPLE_PROCESSING_MESSAGE_EXCEPTION);
        }

        return conversationList.get(0);
    }

    @Override
    @Transactional
    public void conversationClosed(final Long conversationId) {
        OpenAiConversation conversation = openAiConversationRepository.findById(conversationId)
                .orElseThrow(() -> new OpenAiException(ErrorCode.NO_SUCH_CONVERSATION_EXCEPTION));

        if (conversation.isClosed()) {
            throw new OpenAiException(ErrorCode.ALREADY_CLOSED_AI_CONVERSATION_EXCEPTION);
        }

        conversation.conversationClose();
    }

    private OpenAiConversation generateConversation(final Member member) {
        return openAiConversationRepository.save(OpenAiConversation.builder()
                .member(member)
                .status(OpenAiConversationStatus.PROCESSING)
                .build());
    }

}
