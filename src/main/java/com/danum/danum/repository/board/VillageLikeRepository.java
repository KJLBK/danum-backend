package com.danum.danum.repository.board;

import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.board.village.VillageLike;
import com.danum.danum.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VillageLikeRepository extends JpaRepository<VillageLike, Long> {

    Optional<VillageLike> findByVillageIdAndMemberEmail(Village village, Member member);

}
