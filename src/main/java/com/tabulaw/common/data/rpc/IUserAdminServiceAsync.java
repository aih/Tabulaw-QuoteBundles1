/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since May 2, 2010
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tabulaw.model.User;

/**
 * @author jpk
 */
public interface IUserAdminServiceAsync {

	void getAllUsers(AsyncCallback<UserListPayload> callback);

	void createUser(User user, AsyncCallback<ModelPayload<User>> callback);

	void updateUser(User user, AsyncCallback<ModelPayload<User>> callback);

	void deleteUser(String userId, AsyncCallback<Payload> callback);
	
	void setUserPassword(String userId, String password, AsyncCallback<Payload> callback);
}
