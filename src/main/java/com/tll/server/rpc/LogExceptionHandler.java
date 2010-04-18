/**
 * The Logic Lab
 * @author jpk Feb 11, 2009
 */
package com.tll.server.rpc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * LogExceptionHandler - Logs exceptions.
 * @author jpk
 */
public class LogExceptionHandler implements IExceptionHandler {

	private static final Log log = LogFactory.getLog(LogExceptionHandler.class);

	public void handleException(final Throwable t) {
		log.error(t.getMessage(), t);
	}
}
