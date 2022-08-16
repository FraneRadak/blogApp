package com.radak.exceptions;

public class OutOfAuthorities extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OutOfAuthorities(String errorMessage) {
		super(errorMessage);
	}

}
