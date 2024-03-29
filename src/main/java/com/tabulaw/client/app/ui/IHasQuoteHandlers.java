/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since May 9, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

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
