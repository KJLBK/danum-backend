package com.danum.danum.domain.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class TokenBox {

	private TokenDto accessToken;

	private TokenDto refreshToken;

}
