/**
 * The Logic Lab
 * @author jpk
 * @since Apr 4, 2010
 */
package com.tll.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author jpk
 */
public interface IUserContextServiceAsync {

	void getUserContext(AsyncCallback<UserContextPayload> callback);
}
