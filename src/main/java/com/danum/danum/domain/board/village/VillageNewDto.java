package com.danum.danum.domain.board.village;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VillageNewDto {

    private String email;

    private String title;

    private String content;

    private VillagePostType postType;

}
