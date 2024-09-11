package com.danum.danum.service.board.village;

import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.board.village.VillageNewDto;
import com.danum.danum.domain.board.village.VillageViewDto;

import java.util.List;

public interface VillageService {

    void create(VillageNewDto villageNewDto);

    List<VillageViewDto> viewList();

    VillageViewDto view(Long id, String email);

    void likeStatus(Long id, String email);

    List<VillageViewDto> getVillagesByDistance(double latitude, double longitude, double distance);

    List<VillageViewDto> getVillagesByCategory(double latitude, double longitude, String category);

}
