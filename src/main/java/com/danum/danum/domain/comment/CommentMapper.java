package com.danum.danum.domain.comment;

import com.danum.danum.exception.CommentException;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final CommentRepository commentRepository;

    public Comment toEntity(CommentNewDto commentNewDto){
        if (commentNewDto.getMember_email() == null || commentNewDto.getBoard_id() == null) {
            throw new CommentException(ErrorCode.NOT_COMMENT_EXCEPTION);
        }
        CommentId commentId = CommentId.builder()
                .member_email(commentNewDto.getMember_email())
                .board_id(commentNewDto.getBoard_id())
                .build();
        return Comment.builder()
                .commentId(commentId)
                .content(commentNewDto.getContent())
                .created_at(LocalDateTime.now())
                .build();
    }

}
