package com.danum.danum.controller;

import com.danum.danum.exception.CustomException;
import com.danum.danum.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> memberExceptionHandler(CustomException exception) {
		return ResponseEntity
				.status(exception.getHttpStatus())
				.body(
						new ErrorResponse(
							exception.getHttpStatus()
									.value(),
							exception.getMessage()
				));
	}

}
