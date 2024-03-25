package com.danum.danum.service.member;

import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.member.RegisterDto;
import com.danum.danum.domain.member.RegisterMapper;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.MemberException;
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

        validateId(registerDto.getEmail());
        validatePassword(registerDto.getPassword());

        Member member = RegisterMapper.toEntity(registerDto);

        return memberRepository.save(member);

    }

    private void validateId(String email) {

        Optional<Member> member = memberRepository.findByEmail(email);

        if (member.isPresent()) {
            throw new MemberException(ErrorCode.DUPLICATION_EXEPTION);
        }

    }

    private void validatePassword(String password) {

        int passwordLength = password.length();

        if (passwordLength < 8) {
            throw new MemberException(ErrorCode.PASSWORD_SHORT_EXCEPTION);
        }

        if (passwordLength > 16) {
            throw new MemberException(ErrorCode.PASSWORD_LONG_EXCEPTION);
        }

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
