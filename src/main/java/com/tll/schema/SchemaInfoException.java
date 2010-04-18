package com.tll.schema;


/**
 * Intended for use by {@link ISchemaInfo} implementations.
 * @author jpk
 */
public class SchemaInfoException extends RuntimeException {

	private static final long serialVersionUID = 8046311829357995634L;

	/**
	 * 
	 */
	public SchemaInfoException() {
		super();
	}

	/**
	 * @param message
	 */
	public SchemaInfoException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SchemaInfoException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SchemaInfoException(String message, Throwable cause) {
		super(message, cause);
	}

}
