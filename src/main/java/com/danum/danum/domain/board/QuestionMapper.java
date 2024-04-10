package com.danum.danum.domain.board;

import java.time.LocalDateTime;

public class QuestionMapper {

    public static Question toEntity(QuestionNewDto newQuestionDto){

        return Question.builder()
                .email(newQuestionDto.getEmail())
                .title(newQuestionDto.getTitle())
                .content(newQuestionDto.getContent())
                .created_at(LocalDateTime.now())
                .like(0L)
                .count(0L)
                .check(false)
                .build();

    }

}
