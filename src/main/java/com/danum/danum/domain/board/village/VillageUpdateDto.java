package com.danum.danum.domain.board.village;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VillageUpdateDto {
    private Long id;
    private String title;
    private String content;

    public VillageUpdateDto(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
}
