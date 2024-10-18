package com.danum.danum.service.comment;

import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.comment.village.*;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.custom.CommentException;
import com.danum.danum.repository.board.VillageRepository;
import com.danum.danum.repository.comment.VillageCommentRepository;
import com.danum.danum.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VillageCommentServiceImpl implements VillageCommentService {

    private final VillageCommentRepository villageCommentRepository;
    private final VillageCommentMapper villageCommentMapper;
    private final VillageRepository villageRepository;
    private final MemberService memberService;

    @Override
    @Transactional
    public void create(VillageCommentNewDto villageCommentNewDto) {
        validateCommentContent(villageCommentNewDto.getContent());
        VillageComment villageComment = villageCommentMapper.toEntity(villageCommentNewDto);
        villageCommentRepository.save(villageComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VillageCommentViewDto> viewList(Long villageId) {
        return villageCommentRepository.findAllByVillageId(villageId).stream()
                .map(this::convertToViewDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void update(VillageCommentUpdateDto villageCommentUpdateDto, String loginUser) {
        VillageComment villageComment = findCommentAndCheckOwnership(villageCommentUpdateDto.getId(), loginUser);
        villageComment.updateContent(villageCommentUpdateDto.getContent());
        villageCommentRepository.save(villageComment);
    }

    @Override
    @Transactional
    public void delete(Long id, String loginUser) {
        VillageComment villageComment = findCommentAndCheckOwnership(id, loginUser);
        villageCommentRepository.delete(villageComment);
    }

    @Override
    @Transactional
    public void acceptComment(Long villageId, Long commentId, String loginUser) {
        Village village = findVillageAndCheckOwnership(villageId, loginUser);
        checkNoExistingAcceptedComment(village);
        VillageComment comment = findCommentById(commentId);
        acceptCommentAndRewardAuthor(comment);
    }

    @Override
    @Transactional
    public void unacceptComment(Long villageId, Long commentId, String loginUser) {
        findVillageAndCheckOwnership(villageId, loginUser);
        VillageComment comment = findCommentById(commentId);
        unacceptComment(comment);
    }

    private void validateCommentContent(String content) {
        if (content.isEmpty()) {
            throw new CommentException(ErrorCode.COMMENT_NOT_CONTENTS_EXCEPTION);
        }
    }

    private VillageComment findCommentAndCheckOwnership(Long commentId, String loginUser) {
        VillageComment comment = findCommentById(commentId);
        checkCommentOwnership(comment, loginUser);
        return comment;
    }

    private VillageComment findCommentById(Long commentId) {
        return villageCommentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
    }

    private void checkCommentOwnership(VillageComment comment, String loginUser) {
        if (!comment.getMember().getEmail().equals(loginUser)) {
            throw new CommentException(ErrorCode.COMMENT_NOT_AUTHOR_EXCEPTION);
        }
    }

    private Village findVillageAndCheckOwnership(Long villageId, String loginUser) {
        Village village = villageRepository.findById(villageId)
                .orElseThrow(() -> new CommentException(ErrorCode.VILLAGE_NOT_FOUND_EXCEPTION));
        if (!village.getMember().getEmail().equals(loginUser)) {
            throw new CommentException(ErrorCode.NOT_VILLAGE_AUTHOR_EXCEPTION);
        }
        return village;
    }

    private void checkNoExistingAcceptedComment(Village village) {
        if (villageCommentRepository.existsByVillageAndIsAcceptedTrue(village)) {
            throw new CommentException(ErrorCode.COMMENT_ALREADY_ACCEPTED_EXCEPTION);
        }
    }

    private void acceptCommentAndRewardAuthor(VillageComment comment) {
        comment.accept();
        villageCommentRepository.save(comment);
        memberService.exp(comment.getMember().getEmail());
    }

    private void unacceptComment(VillageComment comment) {
        if (!comment.isAccepted()) {
            throw new CommentException(ErrorCode.COMMENT_NOT_ACCEPTED_EXCEPTION);
        }
        comment.unaccept();
        villageCommentRepository.save(comment);
    }

    private VillageCommentViewDto convertToViewDto(VillageComment villageComment) {
        return VillageCommentViewDto.toEntity(villageComment);
    }
}