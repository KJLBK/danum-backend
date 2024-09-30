package com.danum.danum.repository.comment;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.comment.question.QuestionComment;
import com.danum.danum.domain.comment.village.VillageComment;
import com.danum.danum.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VillageCommentRepository extends JpaRepository<VillageComment, Long> {

    List<VillageComment> findAllByVillageId(Long villageId);
    List<VillageComment> findAllByMember(Member member);
    List<VillageComment> findAllByVillage(Village village);
    Optional<VillageComment> findByVillageAndIsAcceptedTrue(Village village);

    Optional<VillageComment> findByVillageIdAndIsAcceptedTrue(Long villageId);

    boolean existsByVillageAndIsAcceptedTrue(Village village);

    long countByVillageAndIsAcceptedTrue(Village village);
}
