package com.danum.danum.domain.board.question;

import com.danum.danum.domain.comment.question.QuestionComment;
import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.openai.OpenAiConversation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_email")
    private Member member;

    @OneToOne
    private OpenAiConversation conversation;

    @Column(name = "question_title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "question_content")
    private String content;

    @Column(name = "question_created_at")
    private LocalDateTime created_at;

    @Column(name = "question_count")
    private Long view_count;

    @Column(name = "question_like")
    private Long like;

    @Column(name = "address_tag")
    private String addressTag;

    // ai 답변을 저장할 필드 추가
    @Column(name = "ai_response", columnDefinition = "TEXT")
    private String aiResponse;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    @Builder.Default
    private List<QuestionComment> questionComments = new ArrayList<>();

    @Transient
    private Boolean hasAcceptedComment;

    public boolean hasAcceptedComment() {
        if (hasAcceptedComment != null) {
            return hasAcceptedComment;
        }
        return false;
    }

    public void setHasAcceptedComment(boolean hasAcceptedComment) {
        this.hasAcceptedComment = hasAcceptedComment;
    }

    // ai 답변을 설정하는 메서드 추가
    public void setAiResponse(String aiResponse) {
        this.aiResponse = aiResponse;
    }

    public void increasedViews() {
        this.view_count++;
    }

    public void addLike() {
        this.like++;
    }

    public void subLike() {
        this.like--;
    }

    public void update(String newContent, String newTitle) {
        this.content = newContent;
        this.title = newTitle;
    }

    public void setAddressTag(String addressTag) {
        this.addressTag = addressTag;
    }
}
