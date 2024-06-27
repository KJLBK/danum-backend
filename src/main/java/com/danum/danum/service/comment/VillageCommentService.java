package com.danum.danum.service.comment;

import com.danum.danum.domain.comment.village.VillageComment;
import com.danum.danum.domain.comment.village.VillageCommentNewDto;
import com.danum.danum.domain.comment.village.VillageCommentUpdateDto;
import com.danum.danum.domain.comment.village.VillageCommentViewDto;

import java.util.List;

public interface VillageCommentService {

    void create(VillageCommentNewDto villageCommentNewDto);

    List<VillageCommentViewDto> viewList(Long id);

    void update(VillageCommentUpdateDto villageCommentUpdateDto, String loginUser);

    void delete(Long id, String loginUser);

}
