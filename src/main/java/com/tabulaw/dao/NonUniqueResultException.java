/**
 * The Logic Lab
 * @author jpk
 * @since Aug 27, 2009
 */
package com.tabulaw.dao;


/**
 * NonUniqueResultException
 * @author jpk
 */
public class NonUniqueResultException extends PersistenceException {

	private static final long serialVersionUID = 2320378838257569253L;

	/**
	 * Constructor
	 * @param message
	 * @param cause
	 */
	public NonUniqueResultException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * @param message
	 */
	public NonUniqueResultException(String message) {
		super(message);
	}

}
