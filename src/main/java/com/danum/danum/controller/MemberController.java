package com.danum.danum.controller;

import com.danum.danum.domain.member.DeleteDto;
import com.danum.danum.domain.member.LoginDto;
import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.member.RegisterDto;
import com.danum.danum.domain.member.UpdateDto;
import com.danum.danum.service.member.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/member/join")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        Member member = memberService.join(registerDto);
        return ResponseEntity.ok(member);
    }

    @PutMapping("/member/update")
    public ResponseEntity<?> update(@RequestBody UpdateDto updateDto){
        Member member = memberService.update(updateDto);
        return ResponseEntity.ok(member);
    }

    @PostMapping("/member/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpServletResponse response){
        String accessToken = memberService.login(loginDto, response);
        return ResponseEntity.ok(accessToken);
    }

    @DeleteMapping("/member/delete")
    public ResponseEntity<?> delete(@RequestBody DeleteDto deleteDto) {
        memberService.delete(deleteDto.getEmail());
        return ResponseEntity.ok("회원탈퇴에 성공하였습니다.");
    }

    @GetMapping("/member/profile-image")
    public ResponseEntity<String> getProfileImage(Authentication authentication) {
        String email = authentication.getName();
        String profileImageUri = memberService.getProfileImageUri(email);
        return ResponseEntity.ok(profileImageUri);
    }
}