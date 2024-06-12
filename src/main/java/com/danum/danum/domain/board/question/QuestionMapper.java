package com.danum.danum.domain.board.question;

import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.openai.OpenAiConversation;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.custom.MemberException;
import com.danum.danum.exception.custom.OpenAiException;
import com.danum.danum.repository.MemberRepository;
import com.danum.danum.repository.OpenAiConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class QuestionMapper {

    private final MemberRepository memberRepository;

    private final OpenAiConversationRepository conversationRepository;

    public Question toEntity(QuestionNewDto questionNewDto){
        String authorEmail = questionNewDto.getEmail();
        Optional<Member> optionalMember = memberRepository.findById(authorEmail);

        if (optionalMember.isEmpty()) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION);
        }

        Member member = optionalMember.get();

        OpenAiConversation conversation = conversationRepository.findById(questionNewDto.getCreateId())
                .orElseThrow(() -> new OpenAiException(ErrorCode.NO_SUCH_CONVERSATION_EXCEPTION));

        return Question.builder()
                .member(member)
                .conversation(conversation)
                .title(questionNewDto.getTitle())
                .content(questionNewDto.getContent())
                .created_at(LocalDateTime.now())
                .like(0L)
                .count(0L).build();
    }

}
