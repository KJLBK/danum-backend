package com.danum.danum.controller;


import com.danum.danum.domain.member.LoginDto;
import com.danum.danum.domain.member.Member;
import com.danum.danum.domain.member.RegisterDto;
import com.danum.danum.domain.member.UpdateDto;
import com.danum.danum.service.member.MemberService;
import com.danum.danum.service.member.MemberServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class MemberController {

    @Autowired
    MemberService memberService;

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

}
