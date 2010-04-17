/**
 * The Logic Lab
 * @author jpk Jan 5, 2008
 */
package com.tabulaw.client.ui.listing;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * IHasListingHandlers - Propagates listing related events.
 * @see IListingHandler
 * @author jpk
 * @param <R> the row element type
 */
public interface IHasListingHandlers<R> extends HasHandlers {
	
	/**
	 * Adds a listing handler.
	 * @param handler
	 * @return handler registration
	 */
	HandlerRegistration addListingHandler(IListingHandler<R> handler);
}
