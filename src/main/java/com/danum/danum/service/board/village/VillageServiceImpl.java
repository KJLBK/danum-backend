package com.danum.danum.service.board.village;

import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.board.village.VillageMapper;
import com.danum.danum.domain.board.village.VillageNewDto;
import com.danum.danum.domain.board.village.view.VillageView;
import com.danum.danum.domain.board.village.view.VillageViewMapper;
import com.danum.danum.domain.member.Member;
import com.danum.danum.exception.BoardException;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.MemberException;
import com.danum.danum.repository.VillageRepository;
import com.danum.danum.repository.VillageViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VillageServiceImpl implements VillageService{

    private final VillageRepository villageRepository;

    private final VillageMapper villageMapper;

    private final VillageViewRepository villageViewRepository;

    private final VillageViewMapper villageViewMapper;

    @Override
    @Transactional
    public void created(VillageNewDto villageNewDto) {
        if (villageNewDto.getEmail().isEmpty()) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION);
        }
        Village village = villageMapper.toEntity(villageNewDto);

        villageRepository.save(village);
    }

    @Override
    @Transactional
    public List<Village> viewList() {
        return villageRepository.findAll();
    }

    @Override
    @Transactional
    public Village view(Long id) {
        Village village = villageRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        Member member = village.getEmail();
        Optional<VillageView> optionalVillageView = villageViewRepository.findByVillageAndMember(village, member);
        if (optionalVillageView.isEmpty()) {
            VillageView villageView = villageViewMapper.toEntity(village, member);
            villageViewRepository.save(villageView);
            village.addView();
            return villageRepository.save(village);
        }

        return village;
    }

    @Override
    @Transactional
    public Long updateLike(Long id) {
        Village village = villageRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        Member member = village.getEmail();
        VillageView villageView = villageViewRepository.findByVillageAndMember(village, member)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        villageView.toggleLiked();
        villageViewRepository.save(villageView);
        if (villageView.isLiked()) {
            village.addLike();
            villageRepository.save(village);
            return village.getLike();
        }

        village.subLike();
        villageRepository.save(village);
        return village.getLike();
    }
}
