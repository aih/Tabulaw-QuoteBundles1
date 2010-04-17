/**
 * The Logic Lab
 * @author jpk
 * Sep 14, 2007
 */
package com.tabulaw.client.model;

import com.google.gwt.event.shared.EventHandler;

/**
 * IModelChangeHandler - Event handler for model change events.
 * @author jpk
 */
public interface IModelChangeHandler extends EventHandler {

	/**
	 * Fired when a model change related event occurs.
	 * @param event The event
	 */
	void onModelChangeEvent(ModelChangeEvent event);
}
