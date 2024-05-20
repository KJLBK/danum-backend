package com.danum.danum.service.comment;

import com.danum.danum.domain.comment.Comment;
import com.danum.danum.domain.comment.CommentDeleteDto;
import com.danum.danum.domain.comment.CommentMapper;
import com.danum.danum.domain.comment.CommentNewDto;
import com.danum.danum.domain.comment.CommentUpdateDto;
import com.danum.danum.domain.comment.CommentViewDto;
import com.danum.danum.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public void created(CommentNewDto commentNewDto) {
        Comment comment = commentMapper.toEntity(commentNewDto);

        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public List<CommentViewDto> commentView(Long id) {
        return commentRepository.findByQuestionId(id).stream()
                .map(comment -> new CommentViewDto(
                        comment.getCommentId().getComment_id(),
                        comment.getCommentId().getMember_email(),
                        comment.getContent())
                )
                .collect(Collectors.toList());
    }

    @Override
    public void update(CommentUpdateDto commentUpdateDto) {
        List<Comment> comments = commentRepository.findByQuestionId(commentUpdateDto.getQuestion_id());
        for (int i = 0; i < comments.size(); i++) {
            if (comments.get(i).getCommentId().equals(commentUpdateDto.getComment_id())) {
                Comment comment = comments.get(i);
                Comment newComment = new Comment(
                        comment.getCommentId()
                        ,comment.getMember()
                        ,comment.getQuestion()
                        ,commentUpdateDto.getContent()
                        ,comment.getCreated_at()
                );
                commentRepository.save(newComment);
                break;
            }
        }
    }

    @Override
    public void delete(CommentDeleteDto commentDeleteDto) {
        List<Comment> comments = commentRepository.findByQuestionId(commentDeleteDto.getQuestion_id());
        for (int i = 0; i < comments.size(); i++) {
            if (comments.get(i).getCommentId().equals(commentDeleteDto.getComment_id())) {
                Comment comment = comments.get(i);
                commentRepository.delete(comment);
            }
        }
    }

}
