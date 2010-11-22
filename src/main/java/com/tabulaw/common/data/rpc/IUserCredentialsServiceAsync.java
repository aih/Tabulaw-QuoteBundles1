/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 4, 2010
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author jpk
 */
public interface IUserCredentialsServiceAsync {

	void registerUser(UserRegistrationRequest request, AsyncCallback<Payload> callback);

	void requestPassword(String emailAddress, AsyncCallback<Payload> callback);
}
