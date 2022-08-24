package com.radak.exceptions;

public class UserRegistrationException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public UserRegistrationException(String errorMessage) {
		super(errorMessage);
	}
}
