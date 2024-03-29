package com.danum.danum.domain.member;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

public class MemberMapper {

    public static Member toEntity(RegisterDto registerDto){

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        return Member.builder()
                .email(registerDto.getEmail())
                .password(registerDto.getPassword())
                .phone(registerDto.getPhone())
                .name(registerDto.getName())
                .exp(0)
                .contribution(0)
                .role(Role.USER)
                .joinDateTime(LocalDateTime.now())
                .build();

    }

}
