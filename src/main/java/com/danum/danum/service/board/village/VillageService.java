package com.danum.danum.service.board.village;

import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.board.village.VillageNewDto;
import com.danum.danum.domain.board.village.VillageViewDto;

import java.util.List;

public interface VillageService {

    void created(VillageNewDto villageNewDto);

    List<VillageViewDto> viewList();

    VillageViewDto view(Long id);

}
