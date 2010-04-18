/**
 * The Logic Lab
 * @author jpk
 * @since Mar 9, 2009
 */
package com.tll.client.validate;

/**
 * IHasErrorHandler - Generic way to get/set an {@link IErrorHandler}.
 * @author jpk
 */
public interface IHasErrorHandler {

	/**
	 * @return the error handler.
	 */
	IErrorHandler getErrorHandler();

	/**
	 * Gets the errror handler.
	 * @param errorHandler
	 */
	void setErrorHandler(IErrorHandler errorHandler);
}
