package com.danum.danum.controller.jwt;

import com.danum.danum.domain.jwt.TokenBox;
import com.danum.danum.domain.jwt.TokenDto;
import com.danum.danum.util.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;

    private final static String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private final static String ACCESS_TOKEN_COOKIE_NAME = "accessToken";

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@RequestBody TokenDto refreshTokenDto, HttpServletResponse response) {
        TokenBox newTokenBox = jwtUtil.refreshAccessToken(refreshTokenDto.getToken());

        // Refresh Token을 쿠키에 설정
        setCookie(response, REFRESH_TOKEN_COOKIE_NAME, newTokenBox.getRefreshToken().getToken());

        // Access Token을 쿠키에 설정
        setCookie(response, ACCESS_TOKEN_COOKIE_NAME, newTokenBox.getAccessToken().getToken());

        return ResponseEntity.ok("토큰 재발급 성공");
    }

    private void setCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}