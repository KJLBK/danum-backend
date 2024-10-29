package com.danum.danum.service.board.village;


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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VillageServiceImpl implements VillageService {

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
        validateMemberExists(villageNewDto.getEmail());
        Village village = villageMapper.toEntity(villageNewDto);
        villageRepository.save(village);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VillageViewDto> viewList(Pageable pageable) {
        return villageRepository.findAll(pageable)
                .map(this::convertToViewDto);
    }

    @Override
    @Transactional
    public VillageViewDto view(Long id, String email) {
        Village village = findVillageById(id);
        processViewCount(village, email);
        return convertToViewDto(village);
    }

    @Override
    @Transactional
    public void likeStatus(Long id, String email) {
        Village village = findVillageById(id);
        Member member = findMemberByEmail(email);
        processLikeStatus(village, member);
    }

    @Override
    @Transactional
    public void deleteVillage(Long id) {
        Village village = findVillageById(id);
        villageRepository.delete(village);
    }

    @Override
    @Transactional
    public void update(VillageUpdateDto villageUpdateDto, String loginUser) {
        Village village = findVillageById(villageUpdateDto.getId());
        validateAuthor(village.getMember().getEmail(), loginUser);
        updateVillageContent(village, villageUpdateDto);
    }

    @Override
    public List<VillageViewDto> getPopularVillages(int limit) {
        return villageRepository.findPopularVillages(PageRequest.of(0, limit))
                .stream()
                .map(this::convertToViewDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VillageViewDto> getVillagesByPostType(VillagePostType postType, Pageable pageable) {
        return villageRepository.findByPostType(postType, pageable)
                .map(this::convertToViewDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAcceptedComment(Long villageId) {
        Village village = findVillageById(villageId);
        return villageCommentRepository.existsByVillageAndIsAcceptedTrue(village);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VillageViewDto> getLocalVillages(String userEmail, Pageable pageable) {
        Member member = findMemberByEmail(userEmail);
        String userAddressTag = AddressParser.parseAddress(member.getAddress());
        return villageRepository.findByAddressTagStartingWith(userAddressTag, pageable)
                .map(this::convertToViewDto);
    }

    private void validateMemberExists(String email) {
        if (email.isEmpty()) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION);
        }
    }

    private Village findVillageById(Long id) {
        return villageRepository.findById(id)
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findById(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));
    }

    private void processViewCount(Village village, String email) {
        boolean hasAlreadyViewed = villageEmailRepository.findById(village.getId())
                .map(token -> token.getEmail().equals(email))
                .orElse(false);

        if (!hasAlreadyViewed) {
            saveNewView(village, email);
        }
    }

    private void saveNewView(Village village, String email) {
        VillageEmailToken token = VillageEmailToken.builder()
                .id(village.getId())
                .email(email)
                .build();

        village.increasedViews();
        villageRepository.save(village);
        villageEmailRepository.save(token);
    }

    private void processLikeStatus(Village village, Member member) {
        Optional<VillageLike> existingLike = villageLikeRepository.findByVillageIdAndMemberEmail(village, member);

        existingLike.ifPresentOrElse(
                like -> removeLike(village, like),
                () -> addLike(village, member)
        );
    }

    private void removeLike(Village village, VillageLike like) {
        village.subLike();
        villageLikeRepository.delete(like);
        villageRepository.save(village);
    }

    private void addLike(Village village, Member member) {
        VillageLike villageLike = VillageLike.builder()
                .villageId(village)
                .memberEmail(member)
                .build();

        village.addLike();
        villageLikeRepository.save(villageLike);
        villageRepository.save(village);
    }

    private void validateAuthor(String author, String loginUser) {
        if (!author.equals(loginUser)) {
            throw new CommentException(ErrorCode.COMMENT_NOT_AUTHOR_EXCEPTION);
        }
    }

    private void updateVillageContent(Village village, VillageUpdateDto updateDto) {
        village.update(updateDto.getContent(), updateDto.getTitle());
        villageRepository.save(village);
    }

    private VillageViewDto convertToViewDto(Village village) {
        return new VillageViewDto().toEntity(village);
    }
}
