package com.danum.danum.domain.comment.question;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCommentId {

    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "member_email")
    private String memberEmail;

}
