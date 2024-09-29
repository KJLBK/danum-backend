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
    INVALID_PROFILE_IMAGE_EXCEPTION(HttpStatus.BAD_REQUEST, "I001", "올바르지 않은 프로필 이미지입니다."),

    BOARD_NOT_FOUND_EXCEPTION(HttpStatus.BAD_REQUEST, "B001", "존재하지 않는 게시판 입니다."),

    COMMENT_NOT_FOUND_EXCEPTION(HttpStatus.BAD_REQUEST, "C001", "존재하지 않는 댓글 입니다."),
    COMMENT_NOT_CONTENTS_EXCEPTION(HttpStatus.BAD_REQUEST, "C002", "댓글 내용이 없습니다."),
    COMMENT_NOT_AUTHOR_EXCEPTION(HttpStatus.BAD_REQUEST, "C003", "작성자가 아닙니다."),

    TOKEN_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "T001", "토큰이 존재하지 않습니다."),
    TOKEN_ROLE_NOT_AVAILABLE_EXCEPTION(HttpStatus.NOT_FOUND, "T002", "토큰 권한이 올바르지 않습니다."),
    TOKEN_EXPIRED_EXCEPTION(HttpStatus.BAD_REQUEST, "T003", "만료된 토큰입니다."),
    TOKEN_SIGNATURE_EXCEPTION(HttpStatus.BAD_REQUEST, "T004", "올바르지 않은 서명입니다."),

    DISCORD_CHANNEL_NOT_FOUND(HttpStatus.BAD_REQUEST, "D001", "올바른 채널이 아닙니다."),

    NO_SUCH_CONVERSATION_EXCEPTION(HttpStatus.BAD_REQUEST, "OA001", "Open Ai 대화를 찾을 수 없습니다."),
    MESSAGE_TYPE_NOT_SUPPORTED_EXCEPTION(HttpStatus.NOT_FOUND, "OA002", "지원되지 않는 메시지 타입입니다."),
    MULTIPLE_PROCESSING_MESSAGE_EXCEPTION(HttpStatus.BAD_REQUEST, "OA003", "진행중인 메시지가 하나보다 많습니다."),
    ALREADY_CLOSED_AI_CONVERSATION_EXCEPTION(HttpStatus.BAD_REQUEST, "OA004", "이미 닫힌 대화입니다.");


    private final HttpStatus status;

    private final String code;

    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

}
