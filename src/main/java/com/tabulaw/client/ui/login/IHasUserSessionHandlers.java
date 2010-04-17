/**
 * The Logic Lab
 * @author jpk Jan 13, 2008
 */
package com.tabulaw.client.ui.login;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Sources user session events.
 * @author jpk
 */
public interface IHasUserSessionHandlers {

	HandlerRegistration addUserSessionHandler(IUserSessionHandler handler);
}
