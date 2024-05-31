package com.danum.danum.repository;

import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.board.village.view.VillageView;
import com.danum.danum.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VillageViewRepository extends JpaRepository<VillageView, Long> {

    Optional<VillageView> findByVillageAndMember(Village question, Member member);

}
