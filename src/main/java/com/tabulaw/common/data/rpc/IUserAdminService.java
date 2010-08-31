/**
 * The Logic Lab
 * @author jpk
 * @since May 2, 2010
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tabulaw.model.User;

/**
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
	 * Creates a single user record.
	 * @param user
	 * @return payload containing the persisted user
	 */
	ModelPayload<User> createUser(User user);

	/**
	 * Updates a single user record.
	 * @param user
	 * @return payload containing the persisted user
	 */
	ModelPayload<User> updateUser(User user);
	
	/**
	 * Deletes a single user record entirely from the system.
	 * @param userId id of the user to delete
	 * @return payload containing the resultant status
	 */
	Payload deleteUser(String userId);
	
	/**
	 * Sets a user's password.
	 * @param userId id of the user for which to set the password
	 * @param password un-encoded new password to set
	 * @return payload containing the resultant status
	 */
	Payload setUserPassword(String userId, String password);
}
