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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final static String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    @Override
    public Member join(RegisterDto registerDto) {
        validateDuplicatedEmailId(registerDto.getEmail());
        validateDuplicatedName(registerDto.getName());

        registerDto.settingPassword(
                passwordEncoder.encode(
                        registerDto.getPassword()));

        Member member = MemberMapper.toEntity(registerDto);

        return memberRepository.save(member);
    }

    private void validateDuplicatedEmailId(String email) {
        Optional<Member> member = memberRepository.findById(email);

        if (member.isPresent()) {
            throw new MemberException(ErrorCode.DUPLICATION_EXCEPTION);
        }
    }

    private void validateDuplicatedName(String name){
        Optional<Member> member = memberRepository.findByName(name);

        if(member.isPresent()){
            throw new MemberException(ErrorCode.NICKNAME_EXCEPTION);
        }
    }

    @Override
    public void delete(String id) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        optionalMember.orElseThrow(() ->
                new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION)
        );

        Member member = optionalMember.get();
        memberRepository.delete(member);
    }

    @Override
    public Member update(UpdateDto updateDto) {
        validateDuplicatedName(updateDto.getName());

        String memberEmail = updateDto.getEmail();
        Optional<Member> optionalMember = memberRepository.findById(memberEmail);
        optionalMember.orElseThrow(() ->
                new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION)
        );

        Member member = optionalMember.get();

        String changePassword = updateDto.getPassword();
        String changePhone = updateDto.getPhone();
        String changeName = updateDto.getName();

        if (StringUtils.hasText(changePassword)) {
            member.updateUserPassword(
                    passwordEncoder.encode(changePassword));
        }
        if (StringUtils.hasText(changePhone)) {
            member.updateUserPhone(changePhone);
        }
        if (StringUtils.hasText(changeName)) {
            member.updateUserName(changeName);
        }

        return memberRepository.save(member);
    }

    @Override
    public String login(LoginDto loginDto, HttpServletResponse response) {
        String memberEmail = loginDto.getEmail();

        Optional<Member> optionalMember = memberRepository.findById(memberEmail);
        optionalMember.orElseThrow(() ->
                new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION)
        );

        Member member = optionalMember.get();

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

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken.getToken()); // 쿠키에 refresh 토큰 저장
        cookie.setHttpOnly(true); // Javascript 접근 방지
        cookie.setSecure(true); // HTTPS 전송만 허용
        cookie.setPath("/"); // 쿠키 사용 경로 설정

        response.addCookie(cookie);

        return accessToken.getToken();
    }

    @Override
    public void logout(HttpServletResponse response) {
       Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
       cookie.setPath("/");
       cookie.setHttpOnly(true);
       cookie.setMaxAge(0);

       response.addCookie(cookie);
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
