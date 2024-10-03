package com.danum.danum.domain.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {
    private String userId;
    private String userName;
    private String profileImageUrl;

    public static AuthorDto from(Member member) {
        return AuthorDto.builder()
                .userId(member.getEmail())
                .userName(member.getName())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}