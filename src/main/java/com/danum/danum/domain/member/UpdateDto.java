package com.danum.danum.domain.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateDto {

    private String email;

    private String password;

    private String phone;

    private String name;

    private String profileImageUri;

}
