/**
 * The Logic Lab
 * @author jpk
 * Sep 14, 2007
 */
package com.tll.client.ui.edit;

import com.google.gwt.event.shared.EventHandler;


/**
 * Listens to edit events of a particular edit content type.
 * @param <T> edit content type
 * @author jpk
 */
public interface IEditHandler<T> extends EventHandler {

	/**
	 * Fired when an edit event occurs.
	 * @param event The event
	 */
	void onEdit(EditEvent<T> event);
}
