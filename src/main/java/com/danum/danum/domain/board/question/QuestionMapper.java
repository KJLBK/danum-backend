package com.danum.danum.domain.board.question;

import com.danum.danum.domain.member.Member;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.custom.MemberException;
import com.danum.danum.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class QuestionMapper {

    private final MemberRepository memberRepository;

    public Question toEntity(QuestionNewDto questionNewDto){
        String authorEmail = questionNewDto.getEmail();
        Optional<Member> optionalMember = memberRepository.findById(authorEmail);

        if (optionalMember.isEmpty()) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION);
        }

        Member member = optionalMember.get();

        return Question.builder()
                .member(member)
                .title(questionNewDto.getTitle())
                .content(questionNewDto.getContent())
                .created_at(LocalDateTime.now())
                .like(0L)
                .count(0L).build();
    }

}
