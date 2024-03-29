package com.danum.danum.service.member;

import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.member.RegisterDto;
import com.danum.danum.domain.member.MemberMapper;
import com.danum.danum.domain.member.UpdateDto;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.MemberException;
import com.danum.danum.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    @Override
    public Member join(RegisterDto registerDto) {
        validateId(registerDto.getEmail());
        validatePassword(registerDto.getPassword());
        validateName(registerDto.getName());

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
    public Member delete(String id) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (optionalMember.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        Member member = optionalMember.get();
        memberRepository.delete(member);

        return member;
    }

    @Override
    public Member update(UpdateDto updateDto) {
        validatePassword(updateDto.getPassword());
        validateName(updateDto.getName());

        Member member = memberRepository.findById(updateDto.getEmail())
                .get();

        member.updateUserPassword(updateDto.getPassword());
        member.updateUserPhone(updateDto.getPhone());
        member.updateUserName(updateDto.getName());

        return memberRepository.save(member);

    }

    @Override
    public Member login(Member member) {
        return new Member();
    }

}
