package com.danum.danum.domain.board.question;

import com.danum.danum.domain.openai.OpenAiConversation;
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

    private OpenAiConversation conversation;

    private Long view_count;

    public static QuestionViewDto from(Question question) {
        return QuestionViewDto.builder()
                .question_id(question.getId())
                .email(question.getMember().getEmail())
                .title(question.getTitle())
                .content(question.getContent())
                .created_at(question.getCreated_at())
                .conversation(question.getConversation())
                .view_count(question.getView_count())
                .build();
    }

}
