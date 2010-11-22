/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Aug 27, 2009
 */
package com.tabulaw.dao;


/**
 * @author jpk
 */
public class EntityExistsException extends PersistenceException {

	private static final long serialVersionUID = -7233687566073361502L;

	/**
	 * Constructor
	 * @param message
	 * @param cause
	 */
	public EntityExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * @param message
	 */
	public EntityExistsException(String message) {
		super(message);
	}

}
