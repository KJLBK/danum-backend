package com.danum.danum.repository;

import com.danum.danum.domain.comment.village.VillageComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VillageCommentRepository extends JpaRepository<VillageComment, Long> {

    List<VillageComment> findAllByVillageId(Long villageId);

}
