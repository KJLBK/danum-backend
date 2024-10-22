package com.danum.danum.service.board.village;

import com.danum.danum.domain.board.village.VillageNewDto;
import com.danum.danum.domain.board.village.VillagePostType;
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

    void deleteVillage(Long id);

    void update(VillageUpdateDto villageUpdateDto, String loginUser);

    List<VillageViewDto> getPopularVillages(int limit);

    Page<VillageViewDto> getVillagesByPostType(VillagePostType postType, Pageable pageable);

    boolean hasAcceptedComment(Long villageId);

    Page<VillageViewDto> getLocalVillages(String userEmail, Pageable pageable);

}
