package com.danum.danum.domain.board.village;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VillageViewDto {

    private Long village_id;

    private String email;

    private String title;

    private String content;

    private LocalDateTime created_at;

    private Long view_count;

    public VillageViewDto toEntity(Village village) {
        return VillageViewDto.builder()
                .village_id(village.getId())
                .email(village.getMember().getEmail())
                .title(village.getTitle())
                .content(village.getContent())
                .created_at(village.getCreated_at())
                .view_count(village.getView_count())
                .build();
    }

}
