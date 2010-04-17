/**
 * The Logic Lab
 * @author jpk
 * @since Apr 4, 2010
 */
package com.tabulaw.client.ui.login;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author jpk
 */
public class UserSessionEvent extends GwtEvent<IUserSessionHandler> {

	public static final com.google.gwt.event.shared.GwtEvent.Type<IUserSessionHandler> TYPE =
			new com.google.gwt.event.shared.GwtEvent.Type<IUserSessionHandler>();

	private final boolean start;

	/**
	 * Constructor
	 * @param start start or end?
	 */
	public UserSessionEvent(boolean start) {
		super();
		this.start = start;
	}

	public boolean isStart() {
		return start;
	}

	@Override
	protected void dispatch(IUserSessionHandler handler) {
		handler.onUserSessionEvent(this);
	}

	@Override
	public Type<IUserSessionHandler> getAssociatedType() {
		return TYPE;
	}

}
