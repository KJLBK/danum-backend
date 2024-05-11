package com.danum.danum.exception;

import com.danum.danum.domain.board.Question;

public class QuestionException extends RuntimeException{

    private ErrorCode errorCode;

    public QuestionException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
