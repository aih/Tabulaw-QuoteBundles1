/**
 * The Logic Lab
 * @author jpk
 * @since Aug 27, 2009
 */
package com.tabulaw.dao;


/**
 * EntityNotFoundException
 * @author jpk
 */
public class EntityNotFoundException extends PersistenceException {

	private static final long serialVersionUID = -7233687566073361502L;

	/**
	 * Constructor
	 * @param message
	 * @param cause
	 */
	public EntityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * @param message
	 */
	public EntityNotFoundException(String message) {
		super(message);
	}

}
