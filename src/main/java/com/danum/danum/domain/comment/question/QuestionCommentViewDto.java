package com.danum.danum.domain.comment.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCommentViewDto {

    private Long comment_id;

    private String email;

    private String content;

    private LocalDateTime created_at;

    public QuestionCommentViewDto toEntity(QuestionComment questionComment) {
        return QuestionCommentViewDto.builder()
                .comment_id(questionComment.getId())
                .email(questionComment.getMember().getEmail())
                .content(questionComment.getContent())
                .created_at(questionComment.getCreated_at())
                .build();
    }

}
