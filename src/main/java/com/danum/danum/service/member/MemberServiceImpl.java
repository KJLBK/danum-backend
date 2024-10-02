package com.danum.danum.service.member;

import com.danum.danum.domain.jwt.TokenBox;
import com.danum.danum.domain.jwt.TokenDto;
import com.danum.danum.domain.member.LoginDto;
import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.member.MemberMapper;
import com.danum.danum.domain.member.RegisterDto;
import com.danum.danum.domain.member.UpdateDto;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.exception.custom.MemberException;
import com.danum.danum.repository.MemberRepository;
import com.danum.danum.util.jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final static String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final int MAX_EXP = 100;

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

    private void validateDuplicatedName(String name) {
        Optional<Member> member = memberRepository.findByName(name);

        if (member.isPresent()) {
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
        String changeProfileImageUrl = updateDto.getProfileImageUrl();  // 추가

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
        if (StringUtils.hasText(changeProfileImageUrl)) {  // 추가
            member.updateProfileImageUrl(changeProfileImageUrl);
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
            throw new MemberException(ErrorCode.PASSWORD_NOT_MATCHED_EXCEPTION);
        }
        if (member.getContribution() == 1) {
            throw new MemberException(ErrorCode.USER_DEACTIVATED_EXCEPTION);
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        TokenBox tokenBox = jwtUtil.generateToken(authentication);
        TokenDto accessToken = tokenBox.getAccessToken();
        TokenDto refreshToken = tokenBox.getRefreshToken();

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken.getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");

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
    public Member getMemberByAuthentication() {
        String id = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Optional<Member> optionalMember = memberRepository.findById(id);
        if (optionalMember.isEmpty()) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION);
        }

        return optionalMember.get();
    }

    @Override
    public Member exp(String email) {
        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));

        if (member.getExp() < MAX_EXP) {
            member.setExp(Math.min(member.getExp() + 1, MAX_EXP));
            return memberRepository.save(member);
        }

        return member;
    }

    @Override
    public Member contribution() {
        return null;
    }

    @Override
    public String getProfileImageUrl(String email) {
        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));
        return member.getProfileImageUrl();
    }

    @Override
    @Transactional
    public void activateMember(String email) {
        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));
        member.activate(); // contribution을 0으로 설정
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void deactivateMember(String email) {
        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION));
        member.deactivate(); // contribution을 1로 설정
        memberRepository.save(member);
    }

    @Override
    public List<String> getAllMemberEmails() {
        return memberRepository.findAll().stream()
                .map(Member::getEmail)
                .collect(Collectors.toList());
    }
}