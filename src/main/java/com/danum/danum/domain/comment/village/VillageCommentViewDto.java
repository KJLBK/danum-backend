package com.danum.danum.domain.comment.village;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VillageCommentViewDto {

    private Long comment_id;

    private String email;

    private String content;

    private LocalDateTime created_at;

    private boolean isAccepted;

    public VillageCommentViewDto toEntity(VillageComment villageComment) {
        return VillageCommentViewDto.builder()
                .comment_id(villageComment.getId())
                .email(villageComment.getMember().getEmail())
                .content(villageComment.getContent())
                .created_at(villageComment.getCreated_at())
                .isAccepted(villageComment.isAccepted())
                .build();
    }

}
