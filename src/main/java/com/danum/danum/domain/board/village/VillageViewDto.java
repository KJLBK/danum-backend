package com.danum.danum.domain.board.village;

import com.danum.danum.domain.member.AuthorDto;
import com.danum.danum.repository.PostDateComparable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VillageViewDto implements PostDateComparable {

    private Long village_id;

    private AuthorDto author;

    private String title;

    private String content;

    private LocalDateTime created_at;

    private Long view_count;

    private Long like;

    private VillagePostType postType;

    private String addressTag;

    public VillageViewDto toEntity(Village village) {
        return VillageViewDto.builder()
                .village_id(village.getId())
                .title(village.getTitle())
                .content(village.getContent())
                .author(AuthorDto.from(village.getMember()))
                .created_at(village.getCreated_at())
                .view_count(village.getView_count())
                .like(village.getLike())
                .postType(village.getPostType())
                .addressTag(village.getAddressTag())
                .build();
    }

}
