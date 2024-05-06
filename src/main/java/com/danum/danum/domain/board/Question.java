package com.danum.danum.domain.board;

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

    @Column(name = "question_email")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_email")
    private String email;

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

}
