package com.danum.danum.exception;

public class CommentException extends RuntimeException{

    private ErrorCode errorCode;

    public CommentException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }

}
