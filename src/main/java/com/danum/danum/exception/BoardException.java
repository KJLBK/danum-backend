package com.danum.danum.exception;

public class BoardException extends RuntimeException{

    private ErrorCode errorCode;

    public BoardException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }

}
