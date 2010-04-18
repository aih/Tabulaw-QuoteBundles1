/**
 * The Logic Lab
 * @author jpk Dec 30, 2007
 */
package com.tll.client.ui;

import com.google.gwt.event.shared.EventHandler;


/**
 * IDragHandler
 * @author jpk
 */
public interface IDragHandler extends EventHandler {

	/**
	 * Fired when a widget undergoes a drag related operation.
	 * @param event The drag event
	 */
	void onDrag(DragEvent event);
}
