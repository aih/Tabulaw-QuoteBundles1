/**
 * The Logic Lab
 * @author jpk
 * Feb 23, 2008
 */
package com.tll.client.data.rpc;

import com.google.gwt.event.shared.HasHandlers;


/**
 * IHasStatusHandlers
 * @author jpk
 */
public interface IHasStatusHandlers extends HasHandlers {

	/**
	 * Adds a handler.
	 * @param listener
	 */
	void addStatusHandler(IStatusHandler listener);

	/**
	 * Removes a handler.
	 * @param listener
	 */
	void removeStatusHandler(IStatusHandler listener);
}
