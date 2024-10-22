package com.danum.danum.service.board.village;

import com.danum.danum.domain.board.question.Question;
import com.danum.danum.domain.board.question.QuestionLike;
import com.danum.danum.domain.board.village.*;
import com.danum.danum.domain.member.Member;
import com.danum.danum.exception.custom.BoardException;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.custom.CommentException;
import com.danum.danum.exception.custom.MemberException;
import com.danum.danum.repository.MemberRepository;
import com.danum.danum.repository.board.VillageEmailRepository;
import com.danum.danum.repository.board.VillageLikeRepository;
import com.danum.danum.repository.board.VillageRepository;
import com.danum.danum.repository.comment.VillageCommentRepository;
import com.danum.danum.util.AddressParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VillageServiceImpl implements VillageService{

    private final VillageRepository villageRepository;

    private final VillageMapper villageMapper;

    private final VillageEmailRepository villageEmailRepository;

    private final VillageLikeRepository villageLikeRepository;

    private final MemberRepository memberRepository;

    private final VillageCommentRepository villageCommentRepository;

    private final AddressParser addressParser;

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
    @Transactional(readOnly = true)
    public Page<VillageViewDto> viewList(Pageable pageable) {
        Page<Village> villagePage = villageRepository.findAll(pageable);
        return villagePage.map(village -> new VillageViewDto().toEntity(village));
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

    @Override
    public void likeStatus(Long id, String email) {
        Village village = villageRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));

        Optional<VillageLike> optionalVillageLike = villageLikeRepository.findByVillageIdAndMemberEmail(village, member);

        if (optionalVillageLike.isPresent()) {
            village.subLike();
            villageLikeRepository.delete(optionalVillageLike.get());
            villageRepository.save(village);
            return;
        }

        VillageLike villageLike = VillageLike.builder()
                .villageId(village)
                .memberEmail(member)
                .build();
        village.addLike();
        villageLikeRepository.save(villageLike);
        villageRepository.save(village);
    }

    private VillageViewDto convertToViewDto(Village village) {
        return new VillageViewDto().toEntity(village);
    }

    @Override
    @Transactional
    public void deleteVillage(Long id) {
        Village village = villageRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        villageRepository.delete(village);
    }

    public void update(VillageUpdateDto villageUpdateDto, String loginUser) {
        Village village = villageRepository.findById(villageUpdateDto.getId())
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
        userCheck(village.getMember().getEmail(), loginUser);
        village.update(villageUpdateDto.getContent(), villageUpdateDto.getTitle());

        villageRepository.save(village);
    }

    public void userCheck(String author, String loginUser) {
        if (!author.equals(loginUser)) {
            throw new CommentException(ErrorCode.COMMENT_NOT_AUTHOR_EXCEPTION);
        }
    }

    @Override
    public List<VillageViewDto> getPopularVillages(int limit) {
        return villageRepository.findPopularVillages(PageRequest.of(0, limit))
                .stream()
                .map(village -> new VillageViewDto().toEntity(village))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VillageViewDto> getVillagesByPostType(VillagePostType postType, Pageable pageable) {
        Page<Village> villagePage = villageRepository.findByPostType(postType, pageable);
        return villagePage.map(this::convertToViewDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAcceptedComment(Long villageId) {
        return villageCommentRepository.existsByVillageAndIsAcceptedTrue(villageRepository.findById(villageId)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VillageViewDto> getLocalVillages(String userEmail, Pageable pageable) {
        Member member = memberRepository.findById(userEmail)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));
        String userAddressTag = AddressParser.parseAddress(member.getAddress());

        Page<Village> localVillages = villageRepository.findByAddressTagStartingWith(userAddressTag, pageable);
        return localVillages.map(this::convertToViewDto);
    }
}
