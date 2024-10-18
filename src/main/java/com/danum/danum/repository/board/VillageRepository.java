package com.danum.danum.repository.board;

import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.member.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VillageRepository extends JpaRepository<Village, Long> {

    @Query("SELECT v FROM Village v WHERE " +
            "6371 * acos(cos(radians(:latitude)) * cos(radians(v.latitude)) * cos(radians(v.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(v.latitude))) <= :distance")
    Page<Village> findVillagesWithinDistance(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("distance") double distance,
            Pageable pageable);

    @Query("SELECT v FROM Village v WHERE " +
            "6371 * acos(cos(radians(:latitude)) * cos(radians(v.latitude)) * cos(radians(v.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(v.latitude))) BETWEEN :minDistance AND :maxDistance")
    Page<Village> findVillagesBetweenDistances(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("minDistance") double minDistance,
            @Param("maxDistance") double maxDistance,
            Pageable pageable);

    @Query("SELECT v FROM Village v WHERE " +
            "6371 * acos(cos(radians(:latitude)) * cos(radians(v.latitude)) * cos(radians(v.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(v.latitude))) > :distance")
    Page<Village> findVillagesBeyondDistance(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("distance") double distance,
            Pageable pageable);

    Page<Village> findAllByMember(Member member, Pageable pageable);

    @Query("SELECT v FROM Village v ORDER BY v.view_count DESC")
    List<Village> findPopularVillages(Pageable pageable);
}
