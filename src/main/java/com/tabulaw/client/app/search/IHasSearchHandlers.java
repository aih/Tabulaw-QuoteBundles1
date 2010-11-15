package com.tabulaw.client.app.search;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface IHasSearchHandlers extends HasHandlers {

	/**
	 * Adds a search handler.
	 * @param handler
	 * @return the registration instance
	 */
	HandlerRegistration addSearchHandler(ISearchHandler handler);
}
