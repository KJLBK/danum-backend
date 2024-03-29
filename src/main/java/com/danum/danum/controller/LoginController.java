package com.danum.danum.controller;

import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserCredentials credentials) {
        // 사용자 이름과 비밀번호를 콘솔에 출력
        System.out.println("Username: " + credentials.getUsername());
        System.out.println("Password: " + credentials.getPassword());

        // 응답 메시지에 사용자 이름을 포함하여 반환
        String responseMessage = "로그인 성공! 사용자: " + credentials.getUsername();
        return ResponseEntity.ok(responseMessage);
    }

    @Getter
    static class UserCredentials {
        private String username;
        private String password;

        public UserCredentials() {
            // 기본 생성자
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
