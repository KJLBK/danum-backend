package com.danum.danum.service.comment;

import com.danum.danum.domain.comment.question.QuestionComment;
import com.danum.danum.domain.comment.question.QuestionCommentMapper;
import com.danum.danum.domain.comment.question.QuestionCommentNewDto;
import com.danum.danum.domain.comment.question.QuestionCommentUpdateDto;
import com.danum.danum.exception.CommentException;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.repository.QuestionCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionCommentServiceImpl implements QuestionCommentService {

    private final QuestionCommentRepository questionCommentRepository;

    private final QuestionCommentMapper questionCommentMapper;

    @Override
    @Transactional
    public void created(QuestionCommentNewDto questionCommentNewDto) {
        QuestionComment questionComment = questionCommentMapper.toEntity(questionCommentNewDto);

        questionCommentRepository.save(questionComment);
    }

    @Override
    @Transactional
    public List<QuestionComment> viewList(Long id) {
        return questionCommentRepository.findAllByQuestionId(id);
    }

    @Override
    @Transactional
    public void update(QuestionCommentUpdateDto questionCommentUpdateDto) {
        QuestionComment questionComment = questionCommentRepository.findByQuestionCommentId_CommentId(questionCommentUpdateDto.getId())
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
        questionComment = questionComment.updateContent(questionCommentUpdateDto.getContent());

        questionCommentRepository.save(questionComment);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        QuestionComment questionComment = questionCommentRepository.findByQuestionCommentId_CommentId(id)
                        .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
        questionCommentRepository.delete(questionComment);
    }

}
