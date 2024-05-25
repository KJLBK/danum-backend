package com.danum.danum.domain.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CommentNewDto {

    private Long board_id;

    private String member_email;

    private String content;

}
