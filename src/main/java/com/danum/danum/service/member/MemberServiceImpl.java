package com.danum.danum.service.member;

import com.danum.danum.domain.member.*;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.MemberException;
import com.danum.danum.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Member join(RegisterDto registerDto) {
        validateId(registerDto.getEmail());
        validatePassword(registerDto.getPassword());
        validateName(registerDto.getName());

        registerDto.settingPassword(encodeP(registerDto.getPassword()));

        Member member = MemberMapper.toEntity(registerDto);

        return memberRepository.save(member);
    }

    private void validateId(String email) {
        Optional<Member> member = memberRepository.findById(email);

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
    public void delete(String id) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (optionalMember.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        Member member = optionalMember.get();

        memberRepository.delete(member);
    }

    @Override
    public Member update(UpdateDto updateDto) {
        validatePassword(updateDto.getPassword());
        validateName(updateDto.getName());

        Member member = memberRepository.findById(updateDto.getEmail()).get();

        if (!updateDto.getPassword().equals("")) {
            member.updateUserPassword(encodeP(updateDto.getPassword()));
        }
        if (!updateDto.getPhone().equals("")) {
            member.updateUserPhone(updateDto.getPhone());
        }
        if (!updateDto.getName().equals("")) {
            member.updateUserName(updateDto.getName());
        }

        return memberRepository.save(member);
    }

    public String encodeP(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public Member login(LoginDto loginDto) {

        Optional<Member> memberOptional = memberRepository.findById(loginDto.getEmail());

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

    @Override
    public Member exp() {
        return null;
    }

    @Override
    public Member contribution() {
        return null;
    }

}
