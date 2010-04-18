/**
 * The Logic Lab
 * @author jpk
 * Feb 23, 2008
 */
package com.tll.client.data.rpc;

import com.google.gwt.event.shared.EventHandler;


/**
 * IStatusHandler
 * @author jpk
 */
public interface IStatusHandler extends EventHandler {

	/**
	 * Fired when a status related event occurrs.
	 * @param event the status event
	 */
	void onStatusEvent(StatusEvent event);
}
