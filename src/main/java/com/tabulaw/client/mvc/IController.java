/**
 * The Logic Lab
 * @author jpk Jan 13, 2008
 */
package com.tabulaw.client.mvc;

import com.tabulaw.client.mvc.view.IViewRequest;

/**
 * IController - Responsible for handling a view request of a particular type.
 * @author jpk
 */
interface IController {

	/**
	 * @param request The view request event
	 * @return true/false
	 */
	boolean canHandle(IViewRequest request);

	/**
	 * @param request
	 */
	void handle(IViewRequest request);
}
