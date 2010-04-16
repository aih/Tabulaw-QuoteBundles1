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
@RemoteServiceRelativePath(value = "userContext")
public interface IUserContextService extends RemoteService {

	/**
	 * This is the init routine that serves to populate the client-side user
	 * context.
	 * @return the payload by which the client-side user context is populated.
	 */
	UserContextPayload getUserContext();
}
