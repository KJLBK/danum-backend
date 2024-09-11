package com.danum.danum.controller.jwt;

import com.danum.danum.exception.ErrorCode;
import com.danum.danum.util.jwt.JwtUtil;
import com.danum.danum.exception.custom.CustomJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            throw new CustomJwtException(ErrorCode.TOKEN_SIGNATURE_EXCEPTION);
        }

        String token = refreshToken.substring(7);
        String newAccessToken = jwtUtil.reissueAccessToken(token);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(newAccessToken);
    }
}