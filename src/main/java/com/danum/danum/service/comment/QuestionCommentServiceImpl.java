package com.danum.danum.service.comment;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.comment.question.QuestionComment;
import com.danum.danum.domain.comment.question.QuestionCommentMapper;
import com.danum.danum.domain.comment.question.QuestionCommentNewDto;
import com.danum.danum.domain.comment.question.QuestionCommentUpdateDto;
import com.danum.danum.domain.comment.question.QuestionCommentViewDto;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.custom.CommentException;
import com.danum.danum.repository.comment.QuestionCommentRepository;
import com.danum.danum.repository.board.QuestionRepository;
import com.danum.danum.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionCommentServiceImpl implements QuestionCommentService {

    private final QuestionRepository questionRepository;

    private final QuestionCommentRepository questionCommentRepository;

    private final QuestionCommentMapper questionCommentMapper;

    private final MemberService memberService;

    @Override
    @Transactional
    public void create(QuestionCommentNewDto questionCommentNewDto) {
        QuestionComment questionComment = questionCommentMapper.toEntity(questionCommentNewDto);

        if (questionCommentNewDto.getContent().isEmpty()) {
            throw new CommentException(ErrorCode.COMMENT_NOT_CONTENTS_EXCEPTION);
        }

        questionCommentRepository.save(questionComment);
    }

    @Override
    @Transactional
    public List<QuestionCommentViewDto> viewList(Long id) {
        List<QuestionComment> questionCommentList = questionCommentRepository.findAllByQuestionId(id);
        List<QuestionCommentViewDto> commentViewList = new ArrayList<>();
        for (QuestionComment questionComment : questionCommentList) {
            commentViewList.add(new QuestionCommentViewDto().toEntity(questionComment));
        }

        return commentViewList;
    }

    @Override
    @Transactional
    public void update(QuestionCommentUpdateDto questionCommentUpdateDto, String loginUser) {
        QuestionComment questionComment = questionCommentRepository.findById(questionCommentUpdateDto.getId())
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
        userCheck(questionComment.getMember().getEmail(), loginUser);
        questionComment.updateContent(questionCommentUpdateDto.getContent());

        questionCommentRepository.save(questionComment);
    }

    @Override
    @Transactional
    public void delete(Long id, String loginUser) {
        QuestionComment questionComment = questionCommentRepository.findById(id)
                        .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
        userCheck(questionComment.getMember().getEmail(), loginUser);

        questionCommentRepository.delete(questionComment);
    }

    public void userCheck(String author, String loginUser) {
        if (author.equals(loginUser)) {
            throw new CommentException(ErrorCode.COMMENT_NOT_AUTHOR_EXCEPTION);
        }
    }

    @Override
    @Transactional
    public void acceptComment(Long questionId, Long commentId, String loginUser) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CommentException(ErrorCode.QUESTION_NOT_FOUND_EXCEPTION));

        if (!question.getMember().getEmail().equals(loginUser)) {
            throw new CommentException(ErrorCode.NOT_QUESTION_AUTHOR_EXCEPTION);
        }

        QuestionComment comment = questionCommentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));

        // 이미 채택된 댓글이 있는지 확인
        if (questionCommentRepository.existsByQuestionAndIsAcceptedTrue(question)) {
            throw new CommentException(ErrorCode.COMMENT_ALREADY_ACCEPTED_EXCEPTION);
        }

        comment.accept(); // 댓글을 채택 상태로 변경
        questionCommentRepository.save(comment);

        // 답변자의 경험치 증가
        memberService.exp(comment.getMember().getEmail());
    }

    @Override
    @Transactional
    public void unacceptComment(Long questionId, Long commentId, String loginUser) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CommentException(ErrorCode.QUESTION_NOT_FOUND_EXCEPTION));

        if (!question.getMember().getEmail().equals(loginUser)) {
            throw new CommentException(ErrorCode.NOT_QUESTION_AUTHOR_EXCEPTION);
        }

        QuestionComment comment = questionCommentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));

        if (!comment.isAccepted()) {
            throw new CommentException(ErrorCode.COMMENT_NOT_ACCEPTED_EXCEPTION);
        }

        comment.unaccept();
        questionCommentRepository.save(comment);
    }

}
