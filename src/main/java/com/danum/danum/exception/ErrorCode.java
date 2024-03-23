package com.danum.danum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    
    PASSWORD_SHORT_EXCEPTION(HttpStatus.BAD_REQUEST, "M001", "비밀번호가 8자보다 적습니다."),
    PASSWORD_LONG_EXCEPTION(HttpStatus.BAD_REQUEST, "M002", "비밀번호가 16자를 초과합니다."),
    DUPLICATION_EXEPTION(HttpStatus.NOT_FOUND, "M003", "이미 중복되는 회원입니다."),
    LEAVE_EXEPTION(HttpStatus.BAD_REQUEST, "M004", "회원 탈퇴에 실패하셨습니다."),
    AWITHOUT_MEMBER(HttpStatus.NOT_FOUND, "M005", "존재하지 않는 사용자입니다."),
    MISTAKE_PASSWORD(HttpStatus.BAD_REQUEST, "M006", "잘못 된 비밀번호 입력입니다.");

    private final HttpStatus status;

    private final String code;

    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
