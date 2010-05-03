/**
 * The Logic Lab
 * @author jpk
 * @since May 2, 2010
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tabulaw.common.data.ModelPayload;
import com.tabulaw.common.model.User;


/**
 * 
 * @author jpk
 */
@RemoteServiceRelativePath(value = "userAdmin")
public interface IUserAdminService extends RemoteService {

	/**
	 * Administration function for fetching the list of users by which to manage
	 * them.
	 * @return list of all registered users.
	 */
	UserListPayload getAllUsers();

	/**
	 * Updates a single user record.
	 * @param user
	 * @return
	 */
	ModelPayload<User> updateUser(User user);
}
