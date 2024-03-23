package com.danum.danum.controller;


import com.danum.danum.domain.MemberDTO;
import com.danum.danum.exception.ErrorCode;
import com.danum.danum.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Controller
public class MemberController {

    @Autowired
    MemberService memberService;

    @PostMapping("/member/join")
    public ResponseEntity<?> register(@RequestBody MemberDTO member){
        memberService.join(member);
        return ResponseEntity.ok("회원 가입에 성공하였습니다.");
    }

    @DeleteMapping("/member/delete")
    public ResponseEntity<?> delete(@RequestBody MemberDTO member){
        memberService.delete(member);
        return ResponseEntity.ok("회원 탈퇴에 성공하였습니다.");
    }


}
