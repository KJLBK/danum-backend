package com.danum.danum.domain.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateDto {

    private String password;

    private String phone;

    private String name;

    private Double latitude;

    private Double longitude;

    private String address;

    private String profileImageUrl;

}
