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
import java.util.stream.Collectors;

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
        // 댓글 내용 유효성 검사
        validateCommentContent(questionCommentNewDto.getContent());
        // DTO를 엔티티로 변환하고 저장
        QuestionComment questionComment = questionCommentMapper.toEntity(questionCommentNewDto);
        questionCommentRepository.save(questionComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionCommentViewDto> viewList(Long questionId) {
        // 질문에 대한 모든 댓글을 조회하고 DTO로 변환
        return questionCommentRepository.findAllByQuestionId(questionId).stream()
                .map(this::convertToViewDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void update(QuestionCommentUpdateDto questionCommentUpdateDto, String loginUser) {
        // 댓글을 찾고 소유권 확인
        QuestionComment questionComment = findCommentAndCheckOwnership(questionCommentUpdateDto.getId(), loginUser);
        // 댓글 내용 업데이트
        questionComment.updateContent(questionCommentUpdateDto.getContent());
        questionCommentRepository.save(questionComment);
    }

    @Override
    @Transactional
    public void delete(Long id, String loginUser) {
        // 댓글을 찾고 소유권 확인
        QuestionComment questionComment = findCommentAndCheckOwnership(id, loginUser);
        // 댓글 삭제
        questionCommentRepository.delete(questionComment);
    }

    @Override
    @Transactional
    public void acceptComment(Long questionId, Long commentId, String loginUser) {
        // 질문을 찾고 소유권 확인
        Question question = findQuestionAndCheckOwnership(questionId, loginUser);
        // 이미 채택된 댓글이 있는지 확인
        checkNoExistingAcceptedComment(question);
        // 댓글 찾기
        QuestionComment comment = findCommentById(commentId);
        // 댓글 채택 및 작성자 보상
        acceptCommentAndRewardAuthor(comment);
    }

    @Override
    @Transactional
    public void unacceptComment(Long questionId, Long commentId, String loginUser) {
        // 질문을 찾고 소유권 확인
        findQuestionAndCheckOwnership(questionId, loginUser);
        // 댓글 찾기
        QuestionComment comment = findCommentById(commentId);
        // 댓글 채택 취소
        unacceptComment(comment);
    }

    // 댓글 내용 유효성 검사
    private void validateCommentContent(String content) {
        if (content.isEmpty()) {
            throw new CommentException(ErrorCode.COMMENT_NOT_CONTENTS_EXCEPTION);
        }
    }

    // 댓글을 찾고 소유권 확인
    private QuestionComment findCommentAndCheckOwnership(Long commentId, String loginUser) {
        QuestionComment comment = findCommentById(commentId);
        checkCommentOwnership(comment, loginUser);
        return comment;
    }

    // 댓글 ID로 댓글 찾기
    private QuestionComment findCommentById(Long commentId) {
        return questionCommentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
    }

    // 댓글 소유권 확인
    private void checkCommentOwnership(QuestionComment comment, String loginUser) {
        if (!comment.getMember().getEmail().equals(loginUser)) {
            throw new CommentException(ErrorCode.COMMENT_NOT_AUTHOR_EXCEPTION);
        }
    }

    // 질문을 찾고 소유권 확인
    private Question findQuestionAndCheckOwnership(Long questionId, String loginUser) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CommentException(ErrorCode.QUESTION_NOT_FOUND_EXCEPTION));
        if (!question.getMember().getEmail().equals(loginUser)) {
            throw new CommentException(ErrorCode.NOT_QUESTION_AUTHOR_EXCEPTION);
        }
        return question;
    }

    // 이미 채택된 댓글이 있는지 확인
    private void checkNoExistingAcceptedComment(Question question) {
        if (questionCommentRepository.existsByQuestionAndIsAcceptedTrue(question)) {
            throw new CommentException(ErrorCode.COMMENT_ALREADY_ACCEPTED_EXCEPTION);
        }
    }

    // 댓글 채택 및 작성자 보상
    private void acceptCommentAndRewardAuthor(QuestionComment comment) {
        comment.accept();
        questionCommentRepository.save(comment);
        memberService.exp(comment.getMember().getEmail());
    }

    // 댓글 채택 취소
    private void unacceptComment(QuestionComment comment) {
        if (!comment.isAccepted()) {
            throw new CommentException(ErrorCode.COMMENT_NOT_ACCEPTED_EXCEPTION);
        }
        comment.unaccept();
        questionCommentRepository.save(comment);
    }

    // QuestionComment 엔티티를 QuestionCommentViewDto로 변환
    private QuestionCommentViewDto convertToViewDto(QuestionComment questionComment) {
        return new QuestionCommentViewDto().toEntity(questionComment);
    }

}
