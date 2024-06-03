package com.danum.danum.domain.comment.village;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class VillageCommentId {

    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "village_id")
    private Long questionId;

    @Column(name = "member_email")
    private String memberEmail;

}
