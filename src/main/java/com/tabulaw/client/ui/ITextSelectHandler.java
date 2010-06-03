/**
 * The Logic Lab
 * @author jpk
 * @since Feb 24, 2010
 */
package com.tabulaw.client.ui;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author jpk
 */
public interface ITextSelectHandler extends EventHandler {

	/**
	 * Called when the doc loads in the containing iframe.
	 * @param frameId id of the iframe tag containing the just loaded doc
	 */
	void onDocFrameLoaded(String frameId);

	/**
	 * @param event
	 */
	void onTextSelect(TextSelectEvent event);
}
