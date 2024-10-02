package com.danum.danum.domain.board.question;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuestionUpdateDto {

    private Long id;

    private String title;

    private String content;

    public QuestionUpdateDto(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

}
