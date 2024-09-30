package com.danum.danum.service.comment;

import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.comment.village.VillageComment;
import com.danum.danum.domain.comment.village.VillageCommentMapper;
import com.danum.danum.domain.comment.village.VillageCommentNewDto;
import com.danum.danum.domain.comment.village.VillageCommentUpdateDto;
import com.danum.danum.domain.comment.village.VillageCommentViewDto;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.custom.CommentException;
import com.danum.danum.repository.board.VillageRepository;
import com.danum.danum.repository.comment.VillageCommentRepository;
import com.danum.danum.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        VillageComment villageComment = villageCommentMapper.toEntity(villageCommentNewDto);

        villageCommentRepository.save(villageComment);
    }

    @Override
    @Transactional
    public List<VillageCommentViewDto> viewList(Long id) {
        List<VillageComment> villageCommentList =  villageCommentRepository.findAllByVillageId(id);
        List<VillageCommentViewDto> villageViewList = new ArrayList<>();
        for (VillageComment villageComment : villageCommentList) {
            villageViewList.add(new VillageCommentViewDto().toEntity(villageComment));
        }

        return villageViewList;
    }

    @Override
    @Transactional
    public void update(VillageCommentUpdateDto villageCommentUpdateDto, String loginUser) {
        VillageComment villageComment = villageCommentRepository.findById(villageCommentUpdateDto.getId())
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
        userCheck(villageComment.getMember().getEmail(), loginUser);
        villageComment.updateContent(villageCommentUpdateDto.getContent());

        villageCommentRepository.save(villageComment);
    }

    @Override
    @Transactional
    public void delete(Long id, String loginUser) {
        VillageComment villageComment = villageCommentRepository.findById(id)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
        userCheck(villageComment.getMember().getEmail(), loginUser);
        villageCommentRepository.delete(villageComment);
    }

    public void userCheck(String author, String loginUser) {
        if (author.equals(loginUser)) {
            throw new CommentException(ErrorCode.COMMENT_NOT_AUTHOR_EXCEPTION);
        }
    }

    @Override
    @Transactional
    public void acceptComment(Long villageId, Long commentId, String loginUser) {
        Village village = villageRepository.findById(villageId)
                .orElseThrow(() -> new CommentException(ErrorCode.VILLAGE_NOT_FOUND_EXCEPTION));

        if (!village.getMember().getEmail().equals(loginUser)) {
            throw new CommentException(ErrorCode.NOT_VILLAGE_AUTHOR_EXCEPTION);
        }

        VillageComment comment = villageCommentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));

        // 이미 채택된 댓글이 있는지 확인
        if (villageCommentRepository.existsByVillageAndIsAcceptedTrue(village)) {
            throw new CommentException(ErrorCode.COMMENT_ALREADY_ACCEPTED_EXCEPTION);
        }

        comment.accept(); // 댓글을 채택 상태로 변경
        villageCommentRepository.save(comment);

        // 답변자의 경험치 증가
        memberService.exp(comment.getMember().getEmail());
    }

    @Override
    @Transactional
    public void unacceptComment(Long villageId, Long commentId, String loginUser) {
        Village village = villageRepository.findById(villageId)
                .orElseThrow(() -> new CommentException(ErrorCode.VILLAGE_NOT_FOUND_EXCEPTION));

        if (!village.getMember().getEmail().equals(loginUser)) {
            throw new CommentException(ErrorCode.NOT_VILLAGE_AUTHOR_EXCEPTION);
        }

        VillageComment comment = villageCommentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));

        if (!comment.isAccepted()) {
            throw new CommentException(ErrorCode.COMMENT_NOT_ACCEPTED_EXCEPTION);
        }

        comment.unaccept();
        villageCommentRepository.save(comment);
    }

}
