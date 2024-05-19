package com.danum.danum.domain.comment;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class CommentViewDto {

    private Long comment_id;

    private String member_email;

    private String content;

}
