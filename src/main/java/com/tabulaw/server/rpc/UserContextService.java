/**
 * The Logic Lab
 * @author jpk Aug 25, 2007
 */
package com.tabulaw.server.rpc;

import java.util.List;

import com.tabulaw.common.data.rpc.IUserContextService;
import com.tabulaw.common.data.rpc.UserContextPayload;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.common.model.User;
import com.tabulaw.server.PersistContext;
import com.tabulaw.server.UserContext;
import com.tll.common.data.Status;
import com.tll.common.msg.Msg.MsgAttr;
import com.tll.common.msg.Msg.MsgLevel;
import com.tll.server.RequestContext;
import com.tll.server.rpc.RpcServlet;

/**
 * @author jpk
 */
public class UserContextService extends RpcServlet implements IUserContextService {

	private static final long serialVersionUID = 7908647379731614097L;

	@Override
	public UserContextPayload getUserContext() {
		final RequestContext rc = getRequestContext();
		final PersistContext pc = (PersistContext) rc.getServletContext().getAttribute(PersistContext.KEY);

		final Status status = new Status();
		UserContextPayload payload = new UserContextPayload(status);

		final UserContext userContext = (UserContext) rc.getSession().getAttribute(UserContext.KEY);
		if(userContext == null || userContext.getUser() == null) {
			// presume not logged in yet
			status.addMsg("User Context not found.", MsgLevel.INFO, MsgAttr.STATUS.flag);
			return payload;
		}

		User user = userContext.getUser();
		payload.setUser(user);

		// get the user's quote bundles
		List<QuoteBundle> bundles = pc.getUserDataService().getBundlesForUser(user.getKey().getId());
		if(bundles != null) {
			payload.setBundles(bundles);
		}

		status.addMsg("Admin Context retrieved.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		return payload;
	}
}
