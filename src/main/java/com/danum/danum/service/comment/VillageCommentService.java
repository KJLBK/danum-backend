package com.danum.danum.service.comment;

import com.danum.danum.domain.comment.village.VillageComment;
import com.danum.danum.domain.comment.village.VillageCommentNewDto;
import com.danum.danum.domain.comment.village.VillageCommentUpdateDto;

import java.util.List;

public interface VillageCommentService {

    void created(VillageCommentNewDto villageCommentNewDto);

    List<VillageComment> viewList(Long id);

    void update(VillageCommentUpdateDto villageCommentUpdateDto);

    void delete(Long id);

}
