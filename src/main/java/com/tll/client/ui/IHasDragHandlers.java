/**
 * The Logic Lab
 * @author jpk Dec 30, 2007
 */
package com.tll.client.ui;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;


/**
 * IHasDragHandlers - A widget that implements this interface sources the events defined by the
 * {@link IDragHandler} interface.
 * @author jpk
 */
public interface IHasDragHandlers extends HasHandlers {

	/**
	 * Adds a handler.
	 * @param handler
	 * @return {@link HandlerRegistration}
	 */
	HandlerRegistration addDragHandler(IDragHandler handler);
}
