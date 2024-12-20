package com.danum.danum.domain.board.question;

import com.danum.danum.domain.member.AuthorDto;
import com.danum.danum.domain.openai.OpenAiConversation;
import com.danum.danum.repository.PostDateComparable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionViewDto implements PostDateComparable {

    private Long question_id;
    private String title;
    private String content;
    private AuthorDto author;
    private LocalDateTime created_at;
    private OpenAiConversation conversation;
    private Long view_count;
    private Long like;
    private String addressTag;
    private Long createId;
    private boolean hasAcceptedComment;

    public static QuestionViewDto from(Question question) {
        QuestionViewDtoBuilder builder = QuestionViewDto.builder()
                .question_id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .author(AuthorDto.from(question.getMember()))
                .created_at(question.getCreated_at())
                .conversation(question.getConversation())
                .view_count(question.getView_count())
                .like(question.getLike())
                .addressTag(question.getAddressTag())
                .hasAcceptedComment(question.hasAcceptedComment());

        OpenAiConversation conversation = question.getConversation();
        if (conversation != null) {
            builder.createId(conversation.getCreateId());
        }

        return builder.build();
    }

}
