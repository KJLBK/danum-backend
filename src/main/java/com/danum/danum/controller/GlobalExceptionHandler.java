package com.danum.danum.controller;

import com.danum.danum.exception.CustomException;
import com.danum.danum.exception.ErrorResponse;
import com.danum.danum.exception.custom.CustomJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> memberExceptionHandler(CustomException exception) {
		return ResponseEntity
				.status(exception.getHttpStatus())
				.body(new ErrorResponse(
						exception.getHttpStatus().value(),
						exception.getMessage()
						)
				);
	}

	@ExceptionHandler(CustomJwtException.class)
	public ResponseEntity<ErrorResponse> jwtExceptionHandler(CustomJwtException exception) {
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorResponse(
						HttpStatus.UNAUTHORIZED.value(),
						exception.getMessage()
						)
				);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> genericExceptionHandler(Exception exception) {
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResponse(
						HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"An unexpected error occurred"
						)
				);
	}
}
