/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Feb 24, 2010
 */
package com.tabulaw.client.app.ui;

import com.google.gwt.event.shared.GwtEvent;
import com.tabulaw.client.app.model.MarkOverlay;

/**
 * DocEvent
 * @author jpk
 */
public class DocEvent extends GwtEvent<IDocHandler> {

	public static final Type<IDocHandler> TYPE = new Type<IDocHandler>();
	
	/**
	 * Signifies the type of doc event that occurrs.
	 * @author jpk
	 */
	public static enum DocEventType {
		TEXT_SELECT,
		DOC_CONTENT_LOADED,
		DOC_CONTENT_UNLOADED;
	}
	
	public static DocEvent createTextSelectEvent(MarkOverlay mark) {
		return new DocEvent(DocEventType.TEXT_SELECT, mark);
	}
	
	public static DocEvent createDocLoadEvent(boolean isLoad) {
		return new DocEvent(isLoad ? DocEventType.DOC_CONTENT_LOADED : DocEventType.DOC_CONTENT_LOADED, null);
	}
	
	private final DocEventType det;

	private final MarkOverlay mark;

	/**
	 * Constructor
	 * @param det doc event type
	 * @param mark
	 */
	private DocEvent(DocEventType det, MarkOverlay mark) {
		super();
		this.det = det;
		this.mark = mark;
	}
	
	public DocEventType getDocEventType() {
		return det;
	}

	public MarkOverlay getMark() {
		return mark;
	}

	@Override
	protected void dispatch(IDocHandler handler) {
		handler.onDocEvent(this);
	}

	@Override
	public GwtEvent.Type<IDocHandler> getAssociatedType() {
		return TYPE;
	}

}
