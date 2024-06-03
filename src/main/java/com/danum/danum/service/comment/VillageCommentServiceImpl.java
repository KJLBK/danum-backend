package com.danum.danum.service.comment;

import com.danum.danum.domain.comment.village.VillageComment;
import com.danum.danum.domain.comment.village.VillageCommentMapper;
import com.danum.danum.domain.comment.village.VillageCommentNewDto;
import com.danum.danum.domain.comment.village.VillageCommentUpdateDto;
import com.danum.danum.exception.CommentException;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.repository.VillageCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VillageCommentServiceImpl implements VillageCommentService {

    private final VillageCommentRepository villageCommentRepository;

    private final VillageCommentMapper villageCommentMapper;

    @Override
    @Transactional
    public void created(VillageCommentNewDto villageCommentNewDto) {
        VillageComment villageComment = villageCommentMapper.toEntity(villageCommentNewDto);

        villageCommentRepository.save(villageComment);
    }

    @Override
    @Transactional
    public List<VillageComment> viewList(Long id) {
        return villageCommentRepository.findAllByVillageId(id);
    }

    @Override
    @Transactional
    public void update(VillageCommentUpdateDto villageCommentUpdateDto) {
        VillageComment villageComment = villageCommentRepository.findByVillageCommentId_CommentId(villageCommentUpdateDto.getId())
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
        villageComment = villageComment.updateContent(villageCommentUpdateDto.getContent());

        villageCommentRepository.save(villageComment);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        VillageComment villageComment = villageCommentRepository.findByVillageCommentId_CommentId(id)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
        villageCommentRepository.delete(villageComment);
    }

}
