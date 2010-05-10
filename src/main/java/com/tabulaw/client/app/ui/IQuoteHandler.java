/**
 * The Logic Lab
 * @author jpk
 * @since May 9, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handles {@link QuoteEvent}s.
 * @author jpk
 */
public interface IQuoteHandler extends EventHandler {

	/**
	 * Fired when a quote related event occurrs.
	 * @param event
	 */
	void onQuoteEvent(QuoteEvent event);
}
