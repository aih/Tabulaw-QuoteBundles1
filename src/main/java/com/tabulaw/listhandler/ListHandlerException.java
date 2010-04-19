package com.tabulaw.listhandler;

/**
 * @author jpk
 */
public class ListHandlerException extends Exception {

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
