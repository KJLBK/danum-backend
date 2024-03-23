package com.danum.danum.controller;


import com.danum.danum.domain.MemberDTO;
import com.danum.danum.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class MemberController {

    @Autowired
    MemberService memberService;

    @PostMapping("/member/join")
    public ResponseEntity<?> register(@RequestBody MemberDTO member){
        return ResponseEntity.ok(memberService.join(member));
    }

    @DeleteMapping("/member/delete")
    public ResponseEntity<?> delete(@RequestBody MemberDTO member){
        return ResponseEntity.ok(memberService.delete(member));
    }

    @PostMapping("/member/login")
    public ResponseEntity<?> login(@RequestBody MemberDTO member){
        return ResponseEntity.ok(memberService.login(member));
    }

}
