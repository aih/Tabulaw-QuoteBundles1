/**
 * The Logic Lab
 * @author jpk Feb 23, 2008
 */
package com.tll.client.data.rpc;

import java.util.ArrayList;

import com.google.gwt.event.shared.GwtEvent;

/**
 * StatusEventDispatcher - Dispatches {@link StatusEvent}s to its subscribed
 * {@link IStatusHandler}s.
 * @author jpk
 */
public final class StatusEventDispatcher implements IHasStatusHandlers {

	/**
	 * StatusHandlerCollection
	 * @author jpk
	 */
	@SuppressWarnings("serial")
	public static class StatusHandlerCollection extends ArrayList<IStatusHandler> {

		public void fire(StatusEvent event) {
			for(final IStatusHandler listener : this) {
				listener.onStatusEvent(event);
			}
		}
	}

	private static StatusEventDispatcher instance;

	public static StatusEventDispatcher get() {
		if(instance == null) {
			instance = new StatusEventDispatcher();
		}
		return instance;
	}

	private final StatusHandlerCollection listeners = new StatusHandlerCollection();

	/**
	 * Constructor
	 */
	private StatusEventDispatcher() {
		super();
	}

	public void addStatusHandler(IStatusHandler listener) {
		listeners.add(listener);
	}

	public void removeStatusHandler(IStatusHandler listener) {
		listeners.remove(listener);
	}

	public void fireEvent(GwtEvent<?> event) {
		if(event.getAssociatedType() == StatusEvent.TYPE) {
			listeners.fire((StatusEvent) event);
		}
	}
}
