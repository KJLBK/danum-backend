package com.danum.danum.controller.jwt;

import com.danum.danum.domain.jwt.TokenBox;
import com.danum.danum.domain.jwt.TokenDto;
import com.danum.danum.exception.CustomException;
import com.danum.danum.exception.ErrorResponse;
import com.danum.danum.util.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenDto refreshTokenDto) {
        try {
            TokenBox newTokenBox = jwtUtil.refreshAccessToken(refreshTokenDto.getToken());
            return ResponseEntity.ok(newTokenBox);
        } catch (CustomException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getHttpStatus().value(), e.getMessage());
            return ResponseEntity
                    .status(e.getHttpStatus())
                    .body(errorResponse);
        }
    }
}