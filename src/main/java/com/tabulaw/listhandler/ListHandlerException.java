package com.tabulaw.listhandler;

import com.tll.ApplicationException;

/**
 * @author jpk
 */
public class ListHandlerException extends ApplicationException {

	private static final long serialVersionUID = -2224092686334362770L;

	/**
	 * Constructor
	 * @param message
	 * @param t
	 */
	public ListHandlerException(String message, Throwable t) {
		super(message, t);
	}

	/**
	 * Constructor
	 * @param message
	 */
	public ListHandlerException(String message) {
		super(message);
	}
}
