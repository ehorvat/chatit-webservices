package com.chatit.exceptions;

public class NoToken extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Parameterless Constructor
	public NoToken() {
	}

	// Constructor that accepts a message
	public NoToken(String message) {
		super(message);
	}

}
