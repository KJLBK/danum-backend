package com.danum.danum.service.ai;

import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.openai.OpenAiConversation;
import com.danum.danum.domain.openai.OpenAiConversationStatus;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.OpenAiException;
import com.danum.danum.repository.OpenAiConversationRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OpenAiConversationServiceImpl implements OpenAiConversationService {

    private final OpenAiConversationRepository openAiConversationRepository;

    @Override
    public OpenAiConversation loadProgressingConversation(final Member member) {
        List<OpenAiConversation> conversationList = openAiConversationRepository.findByMemberAndStatus(member, OpenAiConversationStatus.PROCESSING);

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
        Optional<OpenAiConversation> OptionalConversation = openAiConversationRepository.findById(conversationId);
        if (OptionalConversation.isEmpty()) {
            throw new OpenAiException(ErrorCode.NO_SUCH_CONVERSATION_EXCEPTION);
        }

        OpenAiConversation conversation = OptionalConversation.get();
        conversation.conversationClose();
    }

    private OpenAiConversation generateConversation(final Member member) {
        return openAiConversationRepository.save(OpenAiConversation.builder()
                .member(member)
                .status(OpenAiConversationStatus.PROCESSING)
                .build());
    }

}
