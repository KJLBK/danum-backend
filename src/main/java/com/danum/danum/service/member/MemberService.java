package com.danum.danum.service.member;

import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.member.RegisterDto;
import com.danum.danum.domain.member.UpdateDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface MemberService {

    public Member join(RegisterDto registerDto);

    public Member delete(String id);

    public Member update(UpdateDto member);

    public Member login(Member member);

}
