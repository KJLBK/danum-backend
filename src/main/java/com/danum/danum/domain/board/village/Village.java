package com.danum.danum.domain.board.village;

import com.danum.danum.domain.member.Member;
import jakarta.persistence.*;
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

    @Column(name = "village_title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "village_content")
    private String content;

    @Column(name = "village_created_at")
    private LocalDateTime created_at;

    @Column(name = "village_count")
    private Long view_count;

    @Column(name = "village_like")
    private Long like;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type")
    private VillagePostType postType;

    public void increasedViews() {
        this.view_count++;
    }

    public void addLike() {
        this.like++;
    }

    public void subLike() {
        this.like--;
    }

    public void setLocation(Double latitude, Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void update(String newContent, String newTitle) {
        this.content = newContent;
        this.title = newTitle;
    }

}
