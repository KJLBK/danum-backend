package com.danum.danum.domain.board;

import com.danum.danum.domain.member.Member;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.MemberException;
import com.danum.danum.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BoardMapper {

    private final MemberRepository memberRepository;

    public Board toEntity(BoardNewDto boardNewDto){
        String authorEmail = boardNewDto.getEmail();
        Optional<Member> optionalMember = memberRepository.findById(authorEmail);

        if (optionalMember.isEmpty()) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION);
        }

        Member member = optionalMember.get();

        return Board.builder()
                .email(member)
                .title(boardNewDto.getTitle())
                .content(boardNewDto.getContent())
                .created_at(LocalDateTime.now())
                .like(0L)
                .count(0L)
                .check(false)
                .category(boardNewDto.getCategory())
                .build();
    }

}
