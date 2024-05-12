package com.danum.danum.exception;

import io.jsonwebtoken.JwtException;

public class CustomJwtException extends RuntimeException {

	public CustomJwtException(ErrorCode errorCode) {
		super(errorCode.getMessage());
	}

}
