package com.danum.danum.repository.board;

import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.board.village.VillagePostType;
import com.danum.danum.domain.member.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VillageRepository extends JpaRepository<Village, Long> {
    Page<Village> findAllByMember(Member member, Pageable pageable);
    Page<Village> findByPostType(VillagePostType postType, Pageable pageable);
    Page<Village> findByAddressTagStartingWith(String addressTag, Pageable pageable);

    @Query("SELECT v FROM Village v ORDER BY v.view_count DESC")
    List<Village> findPopularVillages(Pageable pageable);

    @Query("SELECT v FROM Village v ORDER BY v.created_at DESC")
    Page<Village> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT v FROM Village v WHERE v.title LIKE %:keyword% OR v.content LIKE %:keyword% ORDER BY v.created_at DESC")
    Page<Village> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
