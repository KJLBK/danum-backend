package com.danum.danum.domain.board;

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
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_email")
    private Member email;

    @Column(name = "question_title")
    private String title;

    @Column(name = "question_content")
    private String content;

    @Column(name = "question_created_at")
    private LocalDateTime created_at;

    @Column(name = "question_like")
    private Long like;

    @Column(name = "question_count")
    private Long count;

    @Column(name = "question_check")
    private boolean check;

    public void checkState(){
        this.check = true;
    }

    public void addLike() {
        this.like += 1L;
    }

    public void addCount() {
        this.count += 1L;
    }

}
