package com.danum.danum.domain.comment.question;

import com.danum.danum.domain.board.question.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCommentNewDto {

    private Long question_id;

    private String member_email;

    private String content;

}
