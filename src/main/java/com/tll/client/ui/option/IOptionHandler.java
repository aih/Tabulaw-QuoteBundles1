/**
 * The Logic Lab
 * @author jpk Dec 6, 2007
 */
package com.tll.client.ui.option;

import com.google.gwt.event.shared.EventHandler;

/**
 * IOptionHandler - Handles {@link OptionEvent}s.
 * @author jpk
 */
public interface IOptionHandler extends EventHandler {

	/**
	 * @param event The OptionEvent
	 */
	void onOptionEvent(OptionEvent event);
}
