package com.danum.danum.domain.board.question;

import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.openai.OpenAiConversation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_email")
    private Member member;

    @OneToOne
    private OpenAiConversation conversation;

    @Column(name = "question_title")
    private String title;

    @Column(name = "question_content")
    private String content;

    @Column(name = "question_created_at")
    private LocalDateTime created_at;

    @Column(name = "question_count")
    private Long view_count;

    @Column(name = "question_like")
    private Long like;

    public void increasedViews() {
        this.view_count++;
    }

    public void addLike() {
        this.like++;
    }

    public void minLike() {
        this.like--;
    }

}
