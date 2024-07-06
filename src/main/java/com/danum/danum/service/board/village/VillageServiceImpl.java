package com.danum.danum.service.board.village;

import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.board.village.VillageEmailToken;
import com.danum.danum.domain.board.village.VillageMapper;
import com.danum.danum.domain.board.village.VillageNewDto;
import com.danum.danum.domain.board.village.VillageViewDto;
import com.danum.danum.exception.custom.BoardException;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.custom.MemberException;
import com.danum.danum.repository.board.VillageEmailRepository;
import com.danum.danum.repository.board.VillageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VillageServiceImpl implements VillageService{

    private final VillageRepository villageRepository;

    private final VillageMapper villageMapper;

    private final VillageEmailRepository villageEmailRepository;

    @Override
    @Transactional
    public void create(VillageNewDto villageNewDto) {
        if (villageNewDto.getEmail().isEmpty()) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION);
        }
        Village village = villageMapper.toEntity(villageNewDto);

        villageRepository.save(village);
    }

    @Override
    @Transactional
    public List<VillageViewDto> viewList() {
        List<Village> villageList =  villageRepository.findAll();
        List<VillageViewDto> villageViews = new ArrayList<>();
        for (Village village : villageList) {
            villageViews.add(new VillageViewDto().toEntity(village));
        }

        return villageViews;
    }

    @Override
    @Transactional
    public VillageViewDto view(Long id, String email) {
        Village village = villageRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        viewCheck(village, email);

        return new VillageViewDto().toEntity(village);
    }

    private void viewCheck(Village village, String email) {
        Optional<VillageEmailToken> optionalVillageEmailToken = villageEmailRepository.findById(village.getId());
        if (optionalVillageEmailToken.isPresent() && optionalVillageEmailToken.get().getEmail().equals(email)) {
            return;
        }

        VillageEmailToken token = VillageEmailToken.builder()
                .id(village.getId())
                .email(email)
                .build();
        village.increasedViews();

        villageRepository.save(village);
        villageEmailRepository.save(token);
    }

}
