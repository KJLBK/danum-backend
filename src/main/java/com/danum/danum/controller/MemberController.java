package com.danum.danum.controller;


import com.danum.danum.service.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MemberController {

    @Autowired
    MemberService memberService;

}
