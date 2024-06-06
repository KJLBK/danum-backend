package com.danum.danum.domain.comment.question;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_email")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "comment_content")
    private String content;

    @Column(name = "comment_created_at")
    private LocalDateTime created_at;

    public QuestionComment updateContent(String newContent) {
        return QuestionComment.builder()
                .member(this.member)
                .question(this.question)
                .content(newContent)
                .created_at(this.created_at)
                .build();
    }

}
