
package com.tabulaw.service;

/**
 *
 * @author Andrey Levchenko
 */
public class LoginNotAllowedException extends Exception{
	/**
	 * Constructor
	 * @param msg
	 */
	public LoginNotAllowedException(String msg) {
		super(msg);
	}

	/**
	 * Constructor
	 * @param msg
	 * @param t
	 */
	public LoginNotAllowedException(String msg, Throwable t) {
		super(msg, t);
	}

}
