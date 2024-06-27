package com.danum.danum.service.comment;

import com.danum.danum.domain.comment.village.VillageComment;
import com.danum.danum.domain.comment.village.VillageCommentMapper;
import com.danum.danum.domain.comment.village.VillageCommentNewDto;
import com.danum.danum.domain.comment.village.VillageCommentUpdateDto;
import com.danum.danum.domain.comment.village.VillageCommentViewDto;
import com.danum.danum.exception.custom.CommentException;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.repository.VillageCommentRepository;
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
        if (villageComment.getMember().equals(loginUser)) {
            throw new CommentException(ErrorCode.COMMENT_NOT_AUTHOR_EXCEPTION);
        }
        villageComment.updateContent(villageCommentUpdateDto.getContent());

        villageCommentRepository.save(villageComment);
    }

    @Override
    @Transactional
    public void delete(Long id, String loginUser) {
        VillageComment villageComment = villageCommentRepository.findById(id)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
        if (villageComment.getMember().equals(loginUser)) {
            throw new CommentException(ErrorCode.COMMENT_NOT_AUTHOR_EXCEPTION);
        }
        villageCommentRepository.delete(villageComment);
    }

}
