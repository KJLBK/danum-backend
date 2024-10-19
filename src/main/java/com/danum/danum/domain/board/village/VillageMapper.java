package com.danum.danum.domain.board.village;

import com.danum.danum.domain.member.Member;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.custom.MemberException;
import com.danum.danum.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VillageMapper {

    private final MemberRepository memberRepository;

    public Village toEntity(VillageNewDto villageNewDto){
        String authorEmail = villageNewDto.getEmail();
        Optional<Member> optionalMember = memberRepository.findById(authorEmail);

        if (optionalMember.isEmpty()) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION);
        }

        Member member = optionalMember.get();

        return Village.builder()
                .member(member)
                .title(villageNewDto.getTitle())
                .content(villageNewDto.getContent())
                .created_at(LocalDateTime.now())
                .latitude(member.getLatitude())
                .longitude(member.getLongitude()) //게시글 필터링할때 이값으로 필터링 ??
                .view_count(0L)
                .like(0L)
                .postType(villageNewDto.getPostType())
                .build();
    }
}
