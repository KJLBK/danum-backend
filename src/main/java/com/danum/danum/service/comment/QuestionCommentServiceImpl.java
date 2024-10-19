package com.danum.danum.service.comment;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.comment.question.QuestionComment;
import com.danum.danum.domain.comment.question.QuestionCommentMapper;
import com.danum.danum.domain.comment.question.QuestionCommentNewDto;
import com.danum.danum.domain.comment.question.QuestionCommentUpdateDto;
import com.danum.danum.domain.comment.question.QuestionCommentViewDto;
import com.danum.danum.domain.notification.Notification;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.custom.CommentException;
import com.danum.danum.repository.comment.QuestionCommentRepository;
import com.danum.danum.repository.board.QuestionRepository;
import com.danum.danum.service.member.MemberService;
import com.danum.danum.service.notification.NotificationService;
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

    private final NotificationService notificationService;

    @Override
    @Transactional
    public void create(QuestionCommentNewDto questionCommentNewDto) {
        validateCommentContent(questionCommentNewDto.getContent());
        QuestionComment questionComment = questionCommentMapper.toEntity(questionCommentNewDto);
        questionCommentRepository.save(questionComment);

        Question question = questionComment.getQuestion();
        String content = String.format("새로운 댓글: %s", questionComment.getContent());
        String link = "/questions/" + question.getId();
        notificationService.createNotification(question.getMember().getEmail(), content, link, Notification.NotificationType.QUESTION_COMMENT);
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
        QuestionComment questionComment = findCommentAndCheckOwnership(questionCommentUpdateDto.getId(), loginUser);

        questionComment.updateContent(questionCommentUpdateDto.getContent());

        questionCommentRepository.save(questionComment);
    }

    @Override
    @Transactional
    public void delete(Long id, String loginUser) {
        QuestionComment questionComment = findCommentAndCheckOwnership(id, loginUser);
        questionCommentRepository.delete(questionComment);
    }

    @Override
    @Transactional
    public void acceptComment(Long questionId, Long commentId, String loginUser) {
        Question question = findQuestionAndCheckOwnership(questionId, loginUser);
        checkNoExistingAcceptedComment(question);
        QuestionComment comment = findCommentById(commentId);
        acceptCommentAndRewardAuthor(comment);
    }

    @Override
    @Transactional
    public void unacceptComment(Long questionId, Long commentId, String loginUser) {
        findQuestionAndCheckOwnership(questionId, loginUser);
        QuestionComment comment = findCommentById(commentId);
        unacceptComment(comment);
    }

    // 댓글 내용 유효성 검사
    private void validateCommentContent(String content) {
        if (content.isEmpty()) {
            throw new CommentException(ErrorCode.COMMENT_NOT_CONTENTS_EXCEPTION);
        }
    }

    private QuestionComment findCommentAndCheckOwnership(Long commentId, String loginUser) {
        QuestionComment comment = findCommentById(commentId);
        checkCommentOwnership(comment, loginUser);
        return comment;
    }

    private QuestionComment findCommentById(Long commentId) {
        return questionCommentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
    }

    private void checkCommentOwnership(QuestionComment comment, String loginUser) {
        if (!comment.getMember().getEmail().equals(loginUser)) {
            throw new CommentException(ErrorCode.COMMENT_NOT_AUTHOR_EXCEPTION);
        }
    }

    private Question findQuestionAndCheckOwnership(Long questionId, String loginUser) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CommentException(ErrorCode.QUESTION_NOT_FOUND_EXCEPTION));
        if (!question.getMember().getEmail().equals(loginUser)) {
            throw new CommentException(ErrorCode.NOT_QUESTION_AUTHOR_EXCEPTION);
        }
        return question;
    }

    private void checkNoExistingAcceptedComment(Question question) {
        if (questionCommentRepository.existsByQuestionAndIsAcceptedTrue(question)) {
            throw new CommentException(ErrorCode.COMMENT_ALREADY_ACCEPTED_EXCEPTION);
        }
    }

    private void acceptCommentAndRewardAuthor(QuestionComment comment) {
        comment.accept();
        questionCommentRepository.save(comment);
        memberService.exp(comment.getMember().getEmail());
    }

    private void unacceptComment(QuestionComment comment) {
        if (!comment.isAccepted()) {
            throw new CommentException(ErrorCode.COMMENT_NOT_ACCEPTED_EXCEPTION);
        }
        comment.unaccept();
        questionCommentRepository.save(comment);
    }

    // QuestionComment 엔티티를 QuestionCommentViewDto로 변환
    private QuestionCommentViewDto convertToViewDto(QuestionComment questionComment) {
        return QuestionCommentViewDto.toEntity(questionComment);
    }

}
