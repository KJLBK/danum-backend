package com.danum.danum.domain.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardNewDto {

    private String email;

    private String title;

    private String content;

    private Category category;

}
