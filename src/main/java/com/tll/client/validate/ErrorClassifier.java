package com.tll.client.validate;

/**
 * ErrorClassifier - Identifies the error "origin".
 * @author jpk
 */
public enum ErrorClassifier {
	CLIENT,
	SERVER;

	/**
	 * Constructor
	 */
	private ErrorClassifier() {
	}

	public boolean isClient() {
		return this == CLIENT;
	}

	public boolean isServer() {
		return this == SERVER;
	}
}