/**
 * The Logic Lab
 * @author jpk
 * @since Feb 24, 2010
 */
package com.tabulaw.client.ui;

import com.google.gwt.event.shared.EventHandler;

/**
 * ITextSelectHandler
 * @author jpk
 */
public interface ITextSelectHandler extends EventHandler {

	/**
	 * @param event
	 */
	void onTextSelect(TextSelectEvent event);
}
