package com.danum.danum.domain.comment.village;

import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "village_comment")
public class VillageComment {

    @EmbeddedId
    private VillageCommentId villageCommentId;

    @ManyToOne
    @JoinColumn(name = "member_email", insertable = false, updatable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "village_id", insertable = false, updatable = false)
    private Village village;

    @Column(name = "comment_content")
    private String content;

    @Column(name = "comment_created_at")
    private LocalDateTime created_at;

    public VillageComment updateContent(String newContent) {
        return VillageComment.builder()
                .villageCommentId(this.villageCommentId)
                .member(this.member)
                .village(this.village)
                .content(newContent)
                .created_at(this.created_at)
                .build();
    }

}
