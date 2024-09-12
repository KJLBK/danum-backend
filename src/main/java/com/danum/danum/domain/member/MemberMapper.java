package com.danum.danum.domain.member;

import java.time.LocalDateTime;

public class MemberMapper {

    public static Member toEntity(RegisterDto registerDto){

        return Member.builder()
                .email(registerDto.getEmail())
                .password(registerDto.getPassword())
                .phone(registerDto.getPhone())
                .name(registerDto.getName())
                .latitude(registerDto.getLatitude())
                .longitude(registerDto.getLongitude())
                .exp(0)
                .contribution(0)
                .role(Role.USER)
                .joinDateTime(LocalDateTime.now())
                .build();

    }

}
