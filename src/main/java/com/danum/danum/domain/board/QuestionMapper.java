package com.danum.danum.domain.board;

import com.danum.danum.domain.member.Member;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.MemberException;
import com.danum.danum.exception.QuestionException;
import com.danum.danum.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class QuestionMapper {

    private final MemberRepository memberRepository;

    public Question toEntity(QuestionNewDto newQuestionDto){
        String authorEmail = newQuestionDto.getEmail();
        Optional<Member> optionalMember = memberRepository.findById(authorEmail);

        if (optionalMember.isEmpty()) {
            throw new QuestionException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION);
        }

        Member member = optionalMember.get();

        return Question.builder()
                .email(member)
                .title(newQuestionDto.getTitle())
                .content(newQuestionDto.getContent())
                .created_at(LocalDateTime.now())
                .like(0L)
                .count(0L)
                .check(false)
                .category(newQuestionDto.getCategory())
                .build();
    }

}
