/**
 * The Logic Lab
 * @author jpk Aug 25, 2007
 */
package com.tabulaw.server.rpc;

import java.util.ArrayList;
import java.util.List;

import com.tabulaw.common.data.rpc.IUserContextService;
import com.tabulaw.common.data.rpc.UserContextPayload;
import com.tabulaw.common.model.PocEntityType;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.common.model.User;
import com.tabulaw.server.UserContext;
import com.tabulaw.service.entity.UserDataService;
import com.tll.common.data.Status;
import com.tll.common.model.Model;
import com.tll.common.msg.Msg.MsgAttr;
import com.tll.common.msg.Msg.MsgLevel;
import com.tll.server.RequestContext;
import com.tll.server.marshal.MarshalOptions;
import com.tll.server.marshal.Marshaler;
import com.tll.server.rpc.RpcServlet;
import com.tll.server.rpc.entity.PersistContext;

/**
 * 
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

		final Marshaler entityMarshaller = pc.getMarshaler();
		MarshalOptions umo = pc.getMarshalOptionsResolver().resolve(PocEntityType.USER);
		MarshalOptions bmo = pc.getMarshalOptionsResolver().resolve(PocEntityType.QUOTE_BUNDLE);

		User user = userContext.getUser();
		final Model muser = entityMarshaller.marshalEntity(user, umo);
		payload.setUser(muser);
		
		// get the user's quote bundles
		List<QuoteBundle> bundles = 
			pc.getEntityServiceFactory().instance(UserDataService.class).getBundlesForUser(user.getId());
		if(bundles != null) {
			ArrayList<Model> mbundles = new ArrayList<Model>(bundles.size());
			for(QuoteBundle qb : bundles) {
				Model mb = entityMarshaller.marshalEntity(qb, bmo);
				mbundles.add(mb);
			}
			payload.setBundles(mbundles);
		}

		status.addMsg("Admin Context retrieved.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		return payload;
	}
}
