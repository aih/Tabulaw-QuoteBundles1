/**
 * The Logic Lab
 * @author jpk
 * Aug 30, 2007
 */
package com.tabulaw.client.ui.listing;

import com.google.gwt.event.shared.EventHandler;


/**
 * IListingHandler - Listens to listing related events.
 * @param <R> The row data type
 * @author jpk
 */
public interface IListingHandler<R> extends EventHandler {

	/**
	 * Fired when a listing related RPC call returns to client.
	 * @param event The event
	 */
	void onListingEvent(ListingEvent<R> event);
}
