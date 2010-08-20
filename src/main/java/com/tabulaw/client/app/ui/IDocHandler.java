/**
 * The Logic Lab
 * @author jpk
 * @since Feb 24, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author jpk
 */
public interface IDocHandler extends EventHandler {

	/**
	 * @param event
	 */
	void onDocEvent(DocEvent event);
}
