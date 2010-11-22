/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * Jan 3, 2008
 */
package com.tabulaw.client.view;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * IHasViewChangeHandlers
 * @author jpk
 */
public interface IHasViewChangeHandlers extends HasHandlers {

	/**
	 * Adds a view change handler.
	 * @param handler
	 * @return the handler registration
	 */
	HandlerRegistration addViewChangeHandler(IViewChangeHandler handler);
}
