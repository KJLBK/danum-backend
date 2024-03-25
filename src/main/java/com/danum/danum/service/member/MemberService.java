package com.danum.danum.service.member;

import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.member.RegisterDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface MemberService {

    public Member join(RegisterDto registerDto);

    public Optional<Member> delete(Member member);

    public Optional<String> update(Member member);

    public Optional<String> login(Member member);

}
