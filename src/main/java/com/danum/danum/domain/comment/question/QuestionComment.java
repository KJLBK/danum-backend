package com.danum.danum.domain.comment.question;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
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

    @Column(name = "is_accepted")
    private boolean isAccepted = false;

    public boolean isAccepted() {
        return isAccepted;
    }

    public void accept() {
        this.isAccepted = true;
    }

    public void unaccept() {
        this.isAccepted = false;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }

}
