/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 18, 2010
 */
package com.tabulaw.dao;

import com.tabulaw.IMarshalable;


/**
 * 
 * @author jpk
 */
public class PersistenceException extends RuntimeException implements IMarshalable {
	
	private static final long serialVersionUID = -4149870883179460403L;

	public PersistenceException(String message, Throwable cause) {
		super(message, cause);
	}

	public PersistenceException(String message) {
		super(message);
	}

}
