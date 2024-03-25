package com.danum.danum.service;

import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.member.RegisterDto;
import com.danum.danum.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService{

    @Autowired
    MemberRepository memberRepository;


    @Override
    public Member join(RegisterDto registerDto) {

    }

    @Override
    public Optional<Member> delete(Member member) {
        return Optional.empty();
    }

    @Override
    public Optional<String> update(Member member) {
        return Optional.empty();
    }

    @Override
    public Optional<String> login(Member member) {
        return Optional.empty();
    }
}
