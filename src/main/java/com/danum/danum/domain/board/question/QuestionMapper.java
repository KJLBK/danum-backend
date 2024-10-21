package com.danum.danum.domain.board.question;

import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.openai.OpenAiConversation;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.custom.MemberException;
import com.danum.danum.exception.custom.OpenAiException;
import com.danum.danum.repository.MemberRepository;
import com.danum.danum.repository.ai.OpenAiConversationRepository;
import com.danum.danum.util.AddressParser;
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
        String addressTag = AddressParser.parseAddress(member.getAddress());

        Question.QuestionBuilder count = Question.builder()
                .member(member)
                .title(questionNewDto.getTitle())
                .content(questionNewDto.getContent())
                .created_at(LocalDateTime.now())
                .view_count(0L)
                .like(0L)
                .addressTag(addressTag);

        if(questionNewDto.getCreateId() != null) {
            OpenAiConversation conversation = conversationRepository.findById(questionNewDto.getCreateId())
                    .orElseThrow(() -> new OpenAiException(ErrorCode.NO_SUCH_CONVERSATION_EXCEPTION));

            count.conversation(conversation);
        }

        return count.build();
    }

}
