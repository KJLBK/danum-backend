package com.danum.danum.domain.comment.village;

import com.danum.danum.domain.board.village.Village;
import com.danum.danum.domain.member.Member;
import com.danum.danum.exception.BoardException;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.MemberException;
import com.danum.danum.repository.MemberRepository;
import com.danum.danum.repository.VillageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class VillageCommentMapper {

    private final MemberRepository memberRepository;

    private final VillageRepository villageRepository;

    public VillageComment toEntity(VillageCommentNewDto villageCommentNewDto) {
        Member member = memberRepository.findById(villageCommentNewDto.getMember_email())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));
        Village village = villageRepository.findById(villageCommentNewDto.getVillage_id())
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND_EXCEPTION));

        return VillageComment.builder()
                .member(member)
                .village(village)
                .content(villageCommentNewDto.getContent())
                .created_at(LocalDateTime.now())
                .build();
    }

}
