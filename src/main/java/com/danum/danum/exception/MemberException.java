package com.danum.danum.exception;

import com.danum.danum.exception.ErrorCode;
public class MemberException extends RuntimeException{

    private final ErrorCode errorCode;

    public MemberException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
