package com.danum.danum.repository.board;

import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.member.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VillageRepository extends JpaRepository<Village, Long> {
    @Query(value = "SELECT v.* FROM village v " +
            "WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(v.latitude)) * cos(radians(v.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(v.latitude)))) <= :distance " +
            "ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(v.latitude)) * cos(radians(v.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(v.latitude))))",
            nativeQuery = true)
    List<Village> findVillagesWithinDistance(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("distance") double distance);

    List<Village> findAllByMember(Member member);
}
/**
 * 주어진 위치(위도, 경도)로부터 특정 거리 이내에 있는 Village들을 찾아 반환
 *
 * @param latitude 기준 위치의 위도
 * @param longitude 기준 위치의 경도
 * @param distance 검색할 최대 거리 (km)
 * @return 지정된 거리 이내에 있는 Village 목록
 *
 * 이 메서드는 다음과 같은 작업을 수행함
 * 1. 하버사인 공식을 사용하여 두 지점 사이의 거리를 계산
 * 2. 계산된 거리가 지정된 distance 이하인 Village들을 선택
 * 3. 결과를 거리에 따라 오름차순으로 정렬
 */