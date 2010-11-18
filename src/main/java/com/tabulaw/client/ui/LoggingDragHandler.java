/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Feb 22, 2010
 */
package com.tabulaw.client.ui;

import java.util.EventObject;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.log.client.Log;

/**
 * Logs drag events and may be extended to serve as a base drag handler class.
 * @author jpk
 */
public class LoggingDragHandler implements DragHandler {

	private final String name;

	public LoggingDragHandler(String name) {
		super();
		this.name = name;
	}

	private void log(EventObject event) {
		Log.debug(name + ": " + event);

	}

	@Override
	public void onDragEnd(DragEndEvent event) {
		log(event);
	}

	@Override
	public void onDragStart(DragStartEvent event) {
		log(event);
	}

	@Override
	public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
		log(event);
	}

	@Override
	public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {
		log(event);
	}
}