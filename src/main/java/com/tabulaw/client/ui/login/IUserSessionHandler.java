/**
 * The Logic Lab
 * @author jpk Aug 24, 2007
 */
package com.tabulaw.client.ui.login;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author jpk
 */
public interface IUserSessionHandler extends EventHandler {

	/**
	 * @param event
	 */
	void onUserSessionEvent(UserSessionEvent event);
}
