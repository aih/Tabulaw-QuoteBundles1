/**
 * The Logic Lab
 * @author jpk Jan 13, 2008
 */
package com.tll.client.ui;

import com.google.gwt.event.shared.GwtEvent;

/**
 * DragEvent - Event object containing information about UI artifact dragging.
 * @author jpk
 */
public class DragEvent extends GwtEvent<IDragHandler> {

	public static final Type<IDragHandler> TYPE = new Type<IDragHandler>();
	
	public static enum DragMode {
		/**
		 * Dragging starts.
		 */
		START,
		/**
		 * In process dragging.
		 */
		DRAGGING,
		/**
		 * This trigger is necessary as there may be drag gestures that are too fast
		 * for the browser to process and here is where we get a chance to
		 * "clean up" in particular finalizing the position of relevant widgets.
		 */
		END;
	}

	public final DragMode dragMode;
	public final int deltaX, deltaY;

	/**
	 * Constructor
	 * @param dragMode the drag mode
	 */
	public DragEvent(DragMode dragMode) {
		this(dragMode, -1, -1);
	}

	/**
	 * Constructor
	 * @param dragMode the drag mode
	 * @param deltaX
	 * @param deltaY
	 */
	public DragEvent(DragMode dragMode, int deltaX, int deltaY) {
		this.dragMode = dragMode;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}

	@Override
	protected void dispatch(IDragHandler handler) {
		handler.onDrag(this);
	}

	@Override
	public GwtEvent.Type<IDragHandler> getAssociatedType() {
		return TYPE;
	}
}
