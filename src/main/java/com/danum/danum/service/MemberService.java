package com.danum.danum.service;

import com.danum.danum.domain.MemberDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface MemberService {

    public Optional<String> join(MemberDTO member);

    public Optional<MemberDTO> delete(MemberDTO member);

    public Optional<MemberDTO> update(MemberDTO member);

    public Optional<MemberDTO> login(MemberDTO member);

}
