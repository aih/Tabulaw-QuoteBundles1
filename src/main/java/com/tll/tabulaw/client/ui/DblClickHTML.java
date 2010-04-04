/**
 * The Logic Lab
 * @author jpk
 * @since Mar 22, 2010
 */
package com.tll.tabulaw.client.ui;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;

/**
 * Extended HTML widget that handles double-click events.
 * @author jpk
 */
public class DblClickHTML extends HTML implements HasDoubleClickHandlers {

	HandlerRegistration hr;
	
	@Override
	public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
		sinkEvents(Event.ONMOUSEDOWN);
		return addDomHandler(handler, DoubleClickEvent.getType());
	}

	@Override
	public void onBrowserEvent(Event event) {
		// prevent un-wanted text selection which is a side-effect of dbl-clicking
		if (DOM.eventGetType(event) == Event.ONMOUSEDOWN) {
			event.preventDefault();
		}
		super.onBrowserEvent(event);
	}

}