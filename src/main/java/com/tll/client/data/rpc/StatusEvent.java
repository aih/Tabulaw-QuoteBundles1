/**
 * The Logic Lab
 * @author jpk
 * Feb 23, 2008
 */
package com.tll.client.data.rpc;

import com.google.gwt.event.shared.GwtEvent;
import com.tll.common.data.Status;

/**
 * StatusEvent
 * @author jpk
 */
public class StatusEvent extends GwtEvent<IStatusHandler> {

	public static final Type<IStatusHandler> TYPE = new Type<IStatusHandler>();

	private final Status status;

	/**
	 * Constructor
	 * @param status
	 */
	public StatusEvent(Status status) {
		assert status != null;
		this.status = status;
	}

	@Override
	public Type<IStatusHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(IStatusHandler handler) {
		handler.onStatusEvent(this);
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}
}
