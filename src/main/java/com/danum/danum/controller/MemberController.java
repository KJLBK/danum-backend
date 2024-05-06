package com.danum.danum.controller;


import com.danum.danum.domain.member.*;
import com.danum.danum.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
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
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto){
        Member member = memberService.login(loginDto);

        return ResponseEntity.ok(member);

    }

    @DeleteMapping("/member/delete")
    public ResponseEntity<?> delete(@RequestBody DeleteDto deleteDto) {
        memberService.delete(deleteDto.getEmail());

        return ResponseEntity.ok("회원탈퇴에 성공하였습니다.");
    }

}
