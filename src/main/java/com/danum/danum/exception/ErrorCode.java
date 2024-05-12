package com.danum.danum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    
    PASSWORD_SHORT_EXCEPTION(HttpStatus.BAD_REQUEST, "M001", "비밀번호가 8자보다 적습니다."),
    PASSWORD_LONG_EXCEPTION(HttpStatus.BAD_REQUEST, "M002", "비밀번호가 16자를 초과합니다."),
    DUPLICATION_EXCEPTION(HttpStatus.BAD_REQUEST, "M003", "이미 사용되는 아이디입니다."),
    NICKNAME_EXCEPTION(HttpStatus.BAD_REQUEST, "M004", "이미 사용중인 닉네임입니다."),
    PASSWORD_NOT_MATCHED_EXCEPTION(HttpStatus.BAD_REQUEST, "M005", "패스워드가 틀렸습니다."),
    MEMBER_NOT_FOUND_EXCEPTION(HttpStatus.BAD_REQUEST, "M006", "존재하지 않는 회원 입니다."),
    NULL_BOARD_EXCEPTION(HttpStatus.BAD_REQUEST, "B001", "존재하지 않는 게시판 입니다."),
    TOKEN_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "T001", "토큰이 존재하지 않습니다.");

    private final HttpStatus status;

    private final String code;

    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

}
