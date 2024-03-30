package com.danum.danum.service.member;

import com.danum.danum.domain.member.*;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.MemberException;
import com.danum.danum.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    @Autowired
    MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Member join(RegisterDto registerDto) {

        validateId(registerDto.getEmail());
        validatePassword(registerDto.getPassword());
        validateName(registerDto.getName());

        registerDto.settingPassword(passwordEncoder.encode(registerDto.getPassword()));

        Member member = RegisterMapper.toEntity(registerDto);

        return memberRepository.save(member);

    }

    private void validateId(String email) {

        Optional<Member> member = memberRepository.findByEmail(email);

        if (member.isPresent()) {
            throw new MemberException(ErrorCode.DUPLICATION_EXCEPTION);
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

    private void validateName(String name){

        Optional<Member> member = memberRepository.findByName(name);

        if(member.isPresent()){
            throw new MemberException(ErrorCode.NICKNAME_EXCEPTION);
        }

    }

    @Override
    public Optional<Member> delete(Member member) {
        return Optional.empty();
    }

    @Override
    public Member update(UpdateDto updateDto) {

        validatePassword(updateDto.getPassword());
        validateName(updateDto.getName());

        Member member = memberRepository.findByEmail(updateDto.getEmail()).get();
        // 수정 필요
        member.updateUserPassword(updateDto.getPassword());
        member.updateUserPhone(updateDto.getPhone());
        member.updateUserName(updateDto.getName());

        return memberRepository.save(member);

    }

    @Override
    public Member login(LoginDto loginDto) {

        Optional<Member> memberOptional = memberRepository.findByEmail(loginDto.getEmail());

        if (memberOptional.isPresent()) {

            Member member = memberOptional.get();

            if (passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
                return member;
            } else {
                throw new MemberException(ErrorCode.PASSWORD_NOTMATCH);
            }
        }
        throw new MemberException(ErrorCode.NULLID_EXCEPTION);
    }

}
