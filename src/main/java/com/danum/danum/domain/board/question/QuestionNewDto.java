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

    public void setCreateId(Long createId) {
        this.createId = createId;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
