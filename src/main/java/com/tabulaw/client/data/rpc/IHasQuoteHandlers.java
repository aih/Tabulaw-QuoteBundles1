/**
 * The Logic Lab
 * @author jpk
 * @since May 9, 2010
 */
package com.tabulaw.client.data.rpc;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.tabulaw.client.app.ui.IQuoteHandler;

/**
 * Fires quote events.
 * @author jpk
 */
public interface IHasQuoteHandlers extends HasHandlers {

	/**
	 * Adds a quote handler.
	 * @param handler the handler
	 * @return the handler registration
	 */
	HandlerRegistration addQuoteHandler(IQuoteHandler handler);
}
