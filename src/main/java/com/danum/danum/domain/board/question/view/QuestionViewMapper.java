package com.danum.danum.domain.board.question.view;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class QuestionViewMapper {
    public QuestionView toEntity(Question question, Member member) {
        return QuestionView.builder()
                .question(question)
                .member(member)
                .viewed_at(LocalDateTime.now())
                .liked(false).build();
    }

}
