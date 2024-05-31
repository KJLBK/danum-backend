package com.danum.danum.domain.comment.question;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "question_comment")
public class QuestionComment {

    @EmbeddedId
    private QuestionCommentId questionCommentId;

    @ManyToOne
    @JoinColumn(name = "member_email", insertable = false, updatable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private Question question;

    @Column(name = "comment_content")
    private String content;

    @Column(name = "comment_created_at")
    private LocalDateTime created_at;

    public QuestionComment updateContent(String newContent) {
        return QuestionComment.builder()
                .questionCommentId(this.questionCommentId)
                .member(this.member)
                .question(this.question)
                .content(newContent)
                .created_at(this.created_at)
                .build();
    }

}
