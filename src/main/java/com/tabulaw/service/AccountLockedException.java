
package com.tabulaw.service;

/**
 *
 * @author Andrey Levchenko
 */
public class AccountLockedException extends Exception{
	/**
	 * Constructor
	 * @param msg
	 */
	public AccountLockedException(String msg) {
		super(msg);
	}

	/**
	 * Constructor
	 * @param msg
	 * @param t
	 */
	public AccountLockedException(String msg, Throwable t) {
		super(msg, t);
	}

}
