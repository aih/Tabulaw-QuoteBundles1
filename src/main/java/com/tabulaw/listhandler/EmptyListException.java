package com.tabulaw.listhandler;

/**
 * EmptyListException
 * @author jpk
 */
public class EmptyListException extends ListHandlerException {

	private static final long serialVersionUID = 2117348961772560830L;

	public EmptyListException() {
		super("No elements exist in list.");
	}

	public EmptyListException(String message) {
		super(message);
	}

	public EmptyListException(String message, Throwable cause) {
		super(message, cause);
	}
}
