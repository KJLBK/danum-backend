package com.danum.danum.domain.board.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionViewDto {

    private Long question_id;

    private String email;

    private String title;

    private String content;

    private LocalDateTime created_at;

    public QuestionViewDto toEntity(Question question) {
        return QuestionViewDto.builder()
                .question_id(question.getId())
                .email(question.getMember().getEmail())
                .title(question.getTitle())
                .content(question.getContent())
                .created_at(question.getCreated_at())
                .build();
    }

}
