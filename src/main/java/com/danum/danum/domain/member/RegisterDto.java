package com.danum.danum.domain.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    private String email;

    private String password;

    private String phone;

    private String name;

    private Double latitude;

    private Double longitude;

    private String address;

    private String profileImageUrl;

    public void settingPassword(String password){
        this.password = password;
    }

}
