package com.danum.danum.exception;

public class DiscordException extends RuntimeException {

	public DiscordException(ErrorCode errorCode) {
		super(errorCode.getMessage());
	}

}
