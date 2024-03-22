package com.danum.danum.controller;


import com.danum.danum.domain.MemberDTO;
import com.danum.danum.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class MemberController {

    @Autowired
    MemberService memberService;

    @PostMapping("/member/join")
    public ResponseEntity<?> register(@RequestBody MemberDTO member){
        memberService.join(member);
        return ResponseEntity.ok(member);
    }

}
