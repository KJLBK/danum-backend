package com.danum.danum.exception;

import com.danum.danum.exception.ErrorCode;
public class MemberException extends RuntimeException{

    public MemberException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }

}
