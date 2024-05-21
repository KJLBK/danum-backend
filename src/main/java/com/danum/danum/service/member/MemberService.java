package com.danum.danum.service.member;

import com.danum.danum.domain.member.LoginDto;
import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.member.RegisterDto;
import com.danum.danum.domain.member.UpdateDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

    public Member join(RegisterDto registerDto);

    public void delete(String id);

    public Member update(UpdateDto updateDto);

    public String login(LoginDto loginDto, HttpServletResponse response);

    public void logout(HttpServletResponse response);

    public Member exp();

    public Member contribution();

}
