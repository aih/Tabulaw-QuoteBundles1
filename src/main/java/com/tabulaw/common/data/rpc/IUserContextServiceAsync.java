/**
 * The Logic Lab
 * @author jpk
 * @since Apr 4, 2010
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author jpk
 */
public interface IUserContextServiceAsync {

	void getClientUserContext(AsyncCallback<UserContextPayload> callback);
}
