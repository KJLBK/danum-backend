package com.danum.danum.service.member;

import com.danum.danum.domain.jwt.TokenBox;
import com.danum.danum.domain.jwt.TokenDto;
import com.danum.danum.domain.member.LoginDto;
import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.member.MemberMapper;
import com.danum.danum.domain.member.RegisterDto;
import com.danum.danum.domain.member.UpdateDto;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.MemberException;
import com.danum.danum.repository.MemberRepository;
import com.danum.danum.util.jwt.JwtUtil;
import com.danum.danum.util.jwt.MemberUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final MemberRepository memberRepository;

    private final MemberUtil memberUtil;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

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
        Member member = memberUtil.findById(id);
        memberRepository.delete(member);
    }

    @Override
    public Member update(UpdateDto updateDto) {
        validatePassword(updateDto.getPassword());
        validateName(updateDto.getName());

        String memberEmail = updateDto.getEmail();
        Member member = memberUtil.findById(memberEmail);

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
    public String login(LoginDto loginDto, HttpServletResponse response) {
        String memberEmail = loginDto.getEmail();
        Member member = memberUtil.findById(memberEmail);

        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION);
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        TokenBox tokenBox = jwtUtil.generateToken(authentication);
        TokenDto accessToken = tokenBox.getAccessToken();
        TokenDto refreshToken = tokenBox.getRefreshToken();

        Cookie cookie = new Cookie("token", refreshToken.getToken()); // 쿠키에 refresh 토큰 저장
        cookie.setHttpOnly(true); // Javascript 접근 방지
        cookie.setSecure(true); // HTTPS 전송만 허용
        cookie.setPath("/"); // 쿠키 사용 경로 설정

        response.addCookie(cookie);

        return accessToken.getToken();
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
