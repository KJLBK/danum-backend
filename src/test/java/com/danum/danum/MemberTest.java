package com.danum.danum;

import com.danum.danum.domain.member.LoginDto;
import com.danum.danum.domain.member.RegisterDto;
import com.danum.danum.service.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional // 데이터 저장 X
@SpringBootTest
public class MemberTest {

    @Autowired
    MemberService memberService;

    @Test
    public void 회원가입(){

        RegisterDto registerDto = new RegisterDto("id", "password", "010-0000-0000", "나이스");

        memberService.join(registerDto);

    }

    @Test
    public void 로그인(){

        LoginDto loginDto = new LoginDto("test@naver.com", "testpassword");

        memberService.login(loginDto);

    }

}
