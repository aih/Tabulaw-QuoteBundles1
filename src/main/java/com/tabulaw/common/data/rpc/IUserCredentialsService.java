/**
 * The Logic Lab
 * @author jpk
 * @since Apr 4, 2010
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author jpk
 */
@RemoteServiceRelativePath(value = "usercred")
public interface IUserCredentialsService extends RemoteService {

	/**
	 * Request to register a user.
	 * @param request user registration request
	 * @return result of the registration process.
	 */
	Payload registerUser(UserRegistrationRequest request);
	
	/**
	 * Request a registered uer's password.
	 * @param emailAddress
	 * @return the resultant status contained in the returned payload
	 */
	Payload requestPassword(String emailAddress);
}
