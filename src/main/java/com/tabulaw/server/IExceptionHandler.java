/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 28, 2009
 */
package com.tabulaw.server;


/**
 * IExceptionHandler - Handles server-side exceptions for RPC requests.
 * @author jpk
 */
public interface IExceptionHandler {

	/**
	 * Handles a server-side exception.
	 * @param t The exception.
	 */
	void handleException(final Throwable t);

}