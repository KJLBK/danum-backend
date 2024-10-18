package com.danum.danum.service.board.village;

import com.danum.danum.domain.board.village.VillageNewDto;
import com.danum.danum.domain.board.village.VillageUpdateDto;
import com.danum.danum.domain.board.village.VillageViewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface VillageService {

    void create(VillageNewDto villageNewDto);

    Page<VillageViewDto> viewList(Pageable pageable);

    VillageViewDto view(Long id, String email);

    void likeStatus(Long id, String email);

    Page<VillageViewDto> getVillagesByDistance(double latitude, double longitude, double distance, Pageable pageable);

    Page<VillageViewDto> getVillagesByCategory(double latitude, double longitude, String category, Pageable pageable);

    void deleteVillage(Long id);

    void update(VillageUpdateDto villageUpdateDto, String loginUser);

    List<VillageViewDto> getPopularVillages(int limit);
}
