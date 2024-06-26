package com.danum.danum.domain.board.question;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionNewDto {

    private String email;

    private String title;

    private String content;

    private Long createId;

}
