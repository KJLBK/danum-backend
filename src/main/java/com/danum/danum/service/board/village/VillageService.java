package com.danum.danum.service.board.village;

import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.board.village.VillageNewDto;

import java.util.List;

public interface VillageService {

    void created(VillageNewDto villageNewDto);

    List<Village> viewList();

    Village view(Long id);

    boolean updateLike(Long id);

}
