package com.danum.danum.service.member;

import com.danum.danum.domain.member.LoginDto;
import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.member.RegisterDto;
import com.danum.danum.domain.member.UpdateDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface MemberService {

    Member join(RegisterDto registerDto);

    void delete(String id);

    Member update(UpdateDto updateDto);

    String login(LoginDto loginDto, HttpServletResponse response);

    void logout(HttpServletResponse response);

    Member getMemberByAuthentication();

    Member exp(String email);

    Member contribution();

    String getProfileImageUrl(String email);

    List<String> getAllMemberEmails();

    void activateMember(String email);
    void deactivateMember(String email);

}
