package com.danum.danum.domain.board.village;

import com.danum.danum.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "village")
public class Village {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "village_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_email")
    private Member member;

    @Column(name = "village_title")
    private String title;

    @Column(name = "village_content")
    private String content;

    @Column(name = "village_created_at")
    private LocalDateTime created_at;

    @Column(name = "village_count")
    private Long view_count;

    @Column(name = "village_like")
    private Long like;

    public void increasedViews() {
        this.view_count++;
    }

    public void addLike() {
        this.like++;
    }

    public void subLike() {
        this.like--;
    }

}
