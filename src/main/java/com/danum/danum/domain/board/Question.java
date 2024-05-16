package com.danum.danum.domain.board;

import com.danum.danum.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @ManyToOne(fetch = FetchType.EAGER)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

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
