/**
 * The Logic Lab
 * @author jpk
 * Feb 14, 2009
 */
package com.tll.client.ui.option;

import com.google.gwt.event.shared.HandlerRegistration;


/**
 * IHasOptionHandlers
 * @author jpk
 */
public interface IHasOptionHandlers {

	/**
	 * Adds an option handler.
	 * @param handler
	 * @return handler registration
	 */
	HandlerRegistration addOptionHandler(IOptionHandler handler);
}
