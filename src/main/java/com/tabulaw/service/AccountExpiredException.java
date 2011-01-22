
package com.tabulaw.service;

/**
 *
 * @author Andrey Levchenko
 */
public class AccountExpiredException extends Exception{
	/**
	 * Constructor
	 * @param msg
	 */
	public AccountExpiredException (String msg) {
		super(msg);
	}

	/**
	 * Constructor
	 * @param msg
	 * @param t
	 */
	public AccountExpiredException (String msg, Throwable t) {
		super(msg, t);
	}

}
