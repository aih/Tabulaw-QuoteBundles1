/**
 * The Logic Lab
 * @author jpk Jan 13, 2008
 */
package com.tabulaw.client.mvc.view;

import com.google.gwt.event.shared.GwtEvent;

/**
 * ViewChangeEvent
 * @author jpk
 */
public final class ViewChangeEvent extends GwtEvent<IViewChangeHandler> {

	public static ViewChangeEvent viewLoadedEvent(ViewKey loadedViewKey) {
		return new ViewChangeEvent(ViewOp.LOAD, loadedViewKey);
	}

	public static ViewChangeEvent viewUnloadedEvent(ViewKey unloadedViewKey) {
		return new ViewChangeEvent(ViewOp.UNLOAD, unloadedViewKey);
	}

	public static final Type<IViewChangeHandler> TYPE = new Type<IViewChangeHandler>();

	public static enum ViewOp {
		UNLOAD,
		LOAD;
	}

	private final ViewOp op;

	private final ViewKey key;

	/**
	 * Constructor
	 * @param op
	 * @param key
	 */
	private ViewChangeEvent(ViewOp op, ViewKey key) {
		this.op = op;
		this.key = key;
	}

	public ViewOp getOp() {
		return op;
	}

	public ViewKey getKey() {
		return key;
	}

	@Override
	public Type<IViewChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(IViewChangeHandler handler) {
		handler.onViewChange(this);
	}

	@Override
	public String toString() {
		return "ViewChangeEvent [key=" + key + ", op=" + op + "]";
	}
}
