package com.radak.exceptions;

public class YourAccountIsBlocked extends RuntimeException {

	public YourAccountIsBlocked(String errorMessage) {
		super(errorMessage);
	}
}
