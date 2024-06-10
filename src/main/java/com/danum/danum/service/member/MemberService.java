package com.danum.danum.service.member;

import com.danum.danum.domain.member.LoginDto;
import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.member.RegisterDto;
import com.danum.danum.domain.member.UpdateDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

    Member join(RegisterDto registerDto);

    void delete(String id);

    Member update(UpdateDto updateDto);

    String login(LoginDto loginDto, HttpServletResponse response);

    void logout(HttpServletResponse response);

    Member getMemberByAuthentication();

    Member exp();

    Member contribution();

}
