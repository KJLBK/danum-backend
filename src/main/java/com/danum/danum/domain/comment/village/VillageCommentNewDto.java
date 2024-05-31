package com.danum.danum.domain.comment.village;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VillageCommentNewDto {

    private Long village_id;

    private String member_email;

    private String content;

}
