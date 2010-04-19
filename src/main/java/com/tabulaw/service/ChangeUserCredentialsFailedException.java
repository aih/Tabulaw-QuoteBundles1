package com.tabulaw.service;

/**
 * ChangeUserCredentialsFailedException
 * @author jpk
 */
public class ChangeUserCredentialsFailedException extends Exception {

	private static final long serialVersionUID = 1058479941139600488L;

	/**
	 * Constructor
	 * @param msg
	 */
	public ChangeUserCredentialsFailedException(String msg) {
		super(msg);
	}

	/**
	 * Constructor
	 * @param msg
	 * @param t
	 */
	public ChangeUserCredentialsFailedException(String msg, Throwable t) {
		super(msg, t);
	}
}
