/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 24, 2009
 */
package com.tabulaw.client.model;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;


/**
 * IHasModelChangeHandlers
 * @author jpk
 */
public interface IHasModelChangeHandlers extends HasHandlers {

	/**
	 * Adds a model change handler.
	 * @param handler
	 * @return the registration instance
	 */
	HandlerRegistration addModelChangeHandler(IModelChangeHandler handler);
}
