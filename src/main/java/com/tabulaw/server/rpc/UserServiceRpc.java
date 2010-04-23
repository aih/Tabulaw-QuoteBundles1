/**
 * The Logic Lab
 * @author jpk Aug 25, 2007
 */
package com.tabulaw.server.rpc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.mail.MailSendException;

import com.tabulaw.common.data.Payload;
import com.tabulaw.common.data.Status;
import com.tabulaw.common.data.rpc.IUserContextService;
import com.tabulaw.common.data.rpc.IUserCredentialsService;
import com.tabulaw.common.data.rpc.UserContextPayload;
import com.tabulaw.common.data.rpc.UserRegistrationRequest;
import com.tabulaw.common.model.IUserRef;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.common.model.User;
import com.tabulaw.common.model.UserState;
import com.tabulaw.common.msg.Msg.MsgAttr;
import com.tabulaw.common.msg.Msg.MsgLevel;
import com.tabulaw.dao.EntityExistsException;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.mail.IMailContext;
import com.tabulaw.mail.MailManager;
import com.tabulaw.mail.MailRouting;
import com.tabulaw.server.PersistContext;
import com.tabulaw.server.RequestContext;
import com.tabulaw.server.UserContext;
import com.tabulaw.service.ChangeUserCredentialsFailedException;
import com.tabulaw.service.entity.UserService;
import com.tabulaw.util.StringUtil;

/**
 * @author jpk
 */
public class UserServiceRpc extends RpcServlet implements IUserContextService, IUserCredentialsService {

	private static final long serialVersionUID = 7908647379731614097L;

	private static final String EMAIL_TEMPLATE_NAME = "forgot-password";
	
	@Override
	public UserContextPayload getUserContext() {
		final Status status = new Status();
		UserContextPayload payload = new UserContextPayload(status);

		final RequestContext rc = getRequestContext();
		final UserContext userContext = rc.getSession() == null ? null : (UserContext) rc.getSession().getAttribute(UserContext.KEY);
		if(userContext == null || userContext.getUser() == null) {
			// presume not logged in yet
			status.addMsg("User Context not found.", MsgLevel.INFO, MsgAttr.STATUS.flag);
			return payload;
		}

		User user = userContext.getUser();
		payload.setUser(user);

		final PersistContext pc = (PersistContext) rc.getServletContext().getAttribute(PersistContext.KEY);
		
		// get the retained user state if there is one
		UserState userState = pc.getUserDataService().getUserState(user.getId());
		payload.setUserState(userState);
		
		// get the user's quote bundles
		List<QuoteBundle> bundles = pc.getUserDataService().getBundlesForUser(user.getId());
		if(bundles != null) {
			payload.setBundles(bundles);
		}

		status.addMsg("User Context retrieved.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		return payload;
	}

	@Override
	public Payload registerUser(UserRegistrationRequest request) {
		Status status = new Status();
		
		// we are forced to create an http session here in order to access the servlet context
		getRequestContext().getRequest().getSession(true);

		PersistContext persistContext =
				(PersistContext) super.getRequestContext().getServletContext().getAttribute(PersistContext.KEY);
		UserService userService = persistContext.getUserService();

		String emailAddress = request.getEmailAddress();
		String password = request.getPassword();

		try {
			userService.create(emailAddress, password);
		}
		catch(EntityExistsException e) {
			status.addMsg("Email already exists", MsgLevel.ERROR, MsgAttr.EXCEPTION.flag | MsgAttr.FIELD.flag, "userEmail");
		}

		return new Payload(status);
	}

	public Payload requestPassword(final String emailAddress) {
		final Status status = new Status();
		final Payload p = new Payload(status);
		final Map<String, Object> data = new HashMap<String, Object>();

		if(StringUtil.isEmpty(emailAddress)) {
			status.addMsg("An email address must be specified.", MsgLevel.ERROR, MsgAttr.STATUS.flag);
		}
		else {
			PersistContext context =
					(PersistContext) getRequestContext().getServletContext().getAttribute(PersistContext.KEY);
			UserService userService = context.getUserService();
			try {
				final IUserRef user = userService.getUserRef(emailAddress);
				final String rp = userService.resetPassword(user.getUserRefId());
				data.put("username", user.getName());
				data.put("emailAddress", user.getEmailAddress());
				data.put("password", rp);
				final MailManager mailManager = context.getMailManager();
				final MailRouting mr = mailManager.buildAppSenderMailRouting(user.getEmailAddress());
				final IMailContext mailContext = mailManager.buildTextTemplateContext(mr, EMAIL_TEMPLATE_NAME, data);
				mailManager.sendEmail(mailContext);
				status.addMsg("Password reminder email was sent.", MsgLevel.INFO, MsgAttr.STATUS.flag);
			}
			catch(final EntityNotFoundException nfe) {
				exceptionToStatus(nfe, status);
				context.getExceptionHandler().handleException(nfe);
			}
			catch(final ChangeUserCredentialsFailedException e) {
				exceptionToStatus(e, status);
				context.getExceptionHandler().handleException(e);
			}
			catch(final MailSendException mse) {
				exceptionToStatus(mse, status);
				context.getExceptionHandler().handleException(mse);
			}
		}

		return p;
	}
}
