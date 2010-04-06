/**
 * The Logic Lab
 * @author jpk
 * @since Apr 5, 2010
 */
package com.tll.tabulaw.server.rpc;

import java.io.Serializable;

import javax.servlet.http.HttpSession;

import com.tll.common.data.Status;
import com.tll.common.model.Model;
import com.tll.server.RequestContext;
import com.tll.server.rpc.RpcServlet;
import com.tll.tabulaw.common.data.rpc.IUserContextService;
import com.tll.tabulaw.common.data.rpc.UserContextPayload;
import com.tll.tabulaw.common.model.PocEntityType;

/**
 * @author jpk
 */
public class UserContextService extends RpcServlet implements IUserContextService {

	public static class UserContext implements Serializable {

		/**
		 * A unique token to serve as a pointer to an instance of this type.
		 */
		public static final String KEY = UserContext.class.getName();
		
		private static final long serialVersionUID = -5842387902136812951L;

		private String username;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

	}

	private static final long serialVersionUID = 4447419934372553277L;

	@Override
	public UserContextPayload getUserContext() {
		Model mUser = null;
		
		RequestContext rc = getRequestContext();
		HttpSession session = rc.getRequest().getSession(false);
		UserContext userContext = session == null ? null : (UserContext) session.getAttribute(UserContext.KEY);
		
		if(userContext != null) {
			mUser = new Model(PocEntityType.USER);
			mUser.setId("1");
			mUser.setString("username", "tabulaw");
		}
		
		Status status = new Status();
		UserContextPayload payload = new UserContextPayload(status, true, "dev", mUser);
		return payload;
	}

}
