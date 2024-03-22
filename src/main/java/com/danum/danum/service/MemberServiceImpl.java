package com.danum.danum.service;

import com.danum.danum.domain.MemberDTO;
import com.danum.danum.exception.MemberException;
import com.danum.danum.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.danum.danum.exception.ErrorCode;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService{

    @Autowired
    MemberRepository memberRepository;

    @Override
    public Optional<String> join(MemberDTO member) {
        validateMember(member.getEmail());
        passwordLimit(member.getPassword());
        memberRepository.save(member);
        return Optional.ofNullable(member)
                .map(MemberDTO::getEmail);
    }
    private void validateMember(String email){
        memberRepository.findByEmail(email)
                .ifPresent(m -> {
                    throw new MemberException(ErrorCode.DUPLICATION_EXEPTION);
                });
    }
    private void passwordLimit(String password){
        if(password.length() < 8){
            throw  new MemberException(ErrorCode.PASSWORD_SHORT_EXCEPTION);
        }
        if(password.length() > 15){
            throw new MemberException(ErrorCode.PASSWORD_LONG_EXCEPTION);
        }
    }

    @Override
    public Optional<MemberDTO> delete(MemberDTO member) {
        return Optional.empty();
    }

    @Override
    public Optional<MemberDTO> update(MemberDTO member) {
        return Optional.empty();
    }

    @Override
    public Optional<MemberDTO> login(MemberDTO member) {
        return Optional.empty();
    }
}
