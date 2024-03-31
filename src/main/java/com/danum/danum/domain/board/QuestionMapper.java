package com.danum.danum.domain.board;

import java.time.LocalDateTime;

public class QuestionMapper {

    public static Question toEntity(NewQuestionDto newQuestionDto){

        return Question.builder()
                .email(newQuestionDto.getEmail())
                .title(newQuestionDto.getTitle())
                .content(newQuestionDto.getContent())
                .created_at(LocalDateTime.now())
                .like(0)
                .count(0)
                .check(true)
                .build();

    }

}
