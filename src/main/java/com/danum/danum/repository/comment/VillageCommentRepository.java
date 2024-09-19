package com.danum.danum.repository.comment;

import com.danum.danum.domain.comment.village.VillageComment;
import com.danum.danum.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VillageCommentRepository extends JpaRepository<VillageComment, Long> {

    List<VillageComment> findAllByVillageId(Long villageId);
    List<VillageComment> findAllByMember(Member member);

}
