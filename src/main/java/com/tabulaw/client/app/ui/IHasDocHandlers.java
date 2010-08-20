/**
 * The Logic Lab
 * @author jpk
 * June 3, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Fires doc events.
 * @author jpk
 */
public interface IHasDocHandlers extends HasHandlers {

	/**
	 * Adds a handler.
	 * @param handler
	 * @return {@link HandlerRegistration}
	 */
	HandlerRegistration addDocHandler(IDocHandler handler);
}
