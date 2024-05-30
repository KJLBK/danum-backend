package com.danum.danum.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BoardException extends CustomException {

    private HttpStatus httpStatus;

    public BoardException(final ErrorCode errorCode) {
        super(errorCode);
        this.httpStatus = errorCode.getStatus();
    }

}
