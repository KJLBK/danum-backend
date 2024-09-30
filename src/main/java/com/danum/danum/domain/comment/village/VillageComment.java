package com.danum.danum.domain.comment.village;

import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "village_comment")
public class VillageComment {

    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_email")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "village_id")
    private Village village;

    @Column(name = "comment_content")
    private String content;

    @Column(name = "comment_created_at")
    private LocalDateTime created_at;

    @Column(name = "is_accepted")
    private boolean isAccepted = false;

    public void updateContent(String newContent) {
        this.content = newContent;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void accept() {
        this.isAccepted = true;
    }
    public void unaccept() {
        this.isAccepted = false;
    }
}
