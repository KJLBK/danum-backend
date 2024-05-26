package com.danum.danum.domain.comment;

import com.danum.danum.domain.board.Board;
import com.danum.danum.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "board_comment")
public class Comment {

    @EmbeddedId
    private CommentId commentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_email", insertable = false, updatable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "board_id", insertable = false, updatable = false)
    private Board board;

    @Column(name = "comment_content")
    private String content;

    @Column(name = "comment_created_at")
    private LocalDateTime created_at;

}
