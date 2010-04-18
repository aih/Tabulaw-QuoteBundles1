/**
 * The Logic Lab
 * @author jpk
 * Sep 3, 2007
 */
package com.tll.client.mvc.view;

import com.google.gwt.event.shared.EventHandler;


/**
 * IViewChangeHandler - Handles {@link ViewChangeEvent}s.
 * @author jpk
 */
public interface IViewChangeHandler extends EventHandler {

	/**
	 * Fired when a view change event occurrs.
	 * @param event The event
	 */
	void onViewChange(ViewChangeEvent event);
}
