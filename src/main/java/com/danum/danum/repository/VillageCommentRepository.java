package com.danum.danum.repository;

import com.danum.danum.domain.comment.village.VillageComment;
import com.danum.danum.domain.comment.village.VillageCommentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VillageCommentRepository extends JpaRepository<VillageComment, VillageCommentId> {

    List<VillageComment> findAllByVillageId(Long villageId);

    Optional<VillageComment> findByVillageCommentId_CommentId(Long commentId);

}
