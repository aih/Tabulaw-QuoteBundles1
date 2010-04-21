/**
 * The Logic Lab
 * @author jpk
 * @since Apr 4, 2010
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tabulaw.common.data.Payload;

/**
 * @author jpk
 */
public interface IUserCredentialsServiceAsync {

	void registerUser(UserRegistrationRequest request, AsyncCallback<Payload> callback);

	void requestPassword(String emailAddress, AsyncCallback<Payload> callback);
}
