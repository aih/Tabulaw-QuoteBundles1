package com.tabulaw.service;

/**
 *
 * @author Andrey Levchenko
 */
public class InvalidCredentialsException extends Exception {
	/**
	 * Constructor
	 * @param msg
	 */
	public InvalidCredentialsException(String msg) {
		super(msg);
	}

	/**
	 * Constructor
	 * @param msg
	 * @param t
	 */
	public InvalidCredentialsException(String msg, Throwable t) {
		super(msg, t);
	}

}
