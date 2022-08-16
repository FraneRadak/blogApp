package com.radak.exceptions;

public class SomethingWentWrongException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SomethingWentWrongException(String errorMessage) {
		super(errorMessage);
	}
}
