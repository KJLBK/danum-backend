package com.danum.danum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

	private final HttpStatus httpStatus;

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.httpStatus = errorCode.getStatus();
	}

}
