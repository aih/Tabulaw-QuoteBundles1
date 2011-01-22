
package com.tabulaw.service;

/**
 *
 * @author Andrey Levchenko
 */
public class AccountDisabledException extends Exception{
	/**
	 * Constructor
	 * @param msg
	 */
	public AccountDisabledException(String msg) {
		super(msg);
	}

	/**
	 * Constructor
	 * @param msg
	 * @param t
	 */
	public AccountDisabledException(String msg, Throwable t) {
		super(msg, t);
	}

}
