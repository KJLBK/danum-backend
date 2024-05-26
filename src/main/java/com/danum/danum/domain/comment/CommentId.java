package com.danum.danum.domain.comment;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class CommentId implements Serializable {

    private Long comment_id;

    private Long board_id;

    private String member_email;

}
