/**
 * The Logic Lab
 * @author jpk Aug 25, 2007
 */
package com.tabulaw.server.rpc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.springframework.mail.MailSendException;

import com.tabulaw.common.data.ModelPayload;
import com.tabulaw.common.data.Payload;
import com.tabulaw.common.data.Status;
import com.tabulaw.common.data.rpc.IUserAdminService;
import com.tabulaw.common.data.rpc.IUserContextService;
import com.tabulaw.common.data.rpc.IUserCredentialsService;
import com.tabulaw.common.data.rpc.IUserDataService;
import com.tabulaw.common.data.rpc.IdsPayload;
import com.tabulaw.common.data.rpc.UserContextPayload;
import com.tabulaw.common.data.rpc.UserListPayload;
import com.tabulaw.common.data.rpc.UserRegistrationRequest;
import com.tabulaw.common.model.EntityType;
import com.tabulaw.common.model.IUserRef;
import com.tabulaw.common.model.Quote;
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
import com.tabulaw.server.UserContext;
import com.tabulaw.service.ChangeUserCredentialsFailedException;
import com.tabulaw.service.entity.UserDataService;
import com.tabulaw.service.entity.UserService;
import com.tabulaw.util.StringUtil;

/**
 * @author jpk
 */
public class UserServiceRpc extends RpcServlet 
implements IUserContextService, IUserCredentialsService, IUserDataService, IUserAdminService {

	private static final long serialVersionUID = 7908647379731614097L;

	private static final String EMAIL_TEMPLATE_NAME = "forgot-password";

	@Override
	public UserListPayload getAllUsers() {
		final Status status = new Status();
		UserListPayload payload = new UserListPayload(status);

		final PersistContext pc = getPersistContext();
		UserService svc = pc.getUserService();

		List<User> list = svc.getAllUsers();
		
		// clear out password
		if(list != null) {
			for(User user : list) user.setPassword(null);
		}
		
		payload.setUsers(list);

		status.addMsg("Users retrieved.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		return payload;
	}

	@Override
	public ModelPayload<User> updateUser(User user) {
		final Status status = new Status();
		ModelPayload<User> payload = new ModelPayload<User>(status);
		
		final PersistContext pc = getPersistContext();
		UserService svc = pc.getUserService();

		user = svc.updateUser(user);
		
		// clear out password
		user.setPassword(null);
		
		payload.setModel(user);

		status.addMsg("Users updated.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		return payload;
	}

	@Override
	public Payload deleteUser(String userId) {
		final Status status = new Status();
		Payload payload = new Payload(status);
		
		final PersistContext pc = getPersistContext();
		UserService svc = pc.getUserService();

		svc.deleteUser(userId);
		
		status.addMsg("Users updated.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		return payload;
	}

	@Override
	public void saveUserState(UserState userState) {
		final PersistContext pc = getPersistContext();
		UserDataService svc = pc.getUserDataService();
		svc.saveUserState(userState);
	}

	@Override
	public UserContextPayload getClientUserContext() {
		final Status status = new Status();
		UserContextPayload payload = new UserContextPayload(status);

		final UserContext userContext = getUserContext();
		if(userContext == null || userContext.getUser() == null) {
			// presume not logged in yet
			status.addMsg("User Context not found.", MsgLevel.INFO, MsgAttr.STATUS.flag);
			return payload;
		}

		User user = userContext.getUser();
		payload.setUser(user);

		final PersistContext pc = getPersistContext();

		// get the retained user state if there is one
		try {
			UserState userState = pc.getUserDataService().getUserState(user.getId());
			payload.setUserState(userState);
		}
		catch(EntityNotFoundException e) {
			// ok
		}

		// get the user's quote bundles
		List<QuoteBundle> bundles = pc.getUserDataService().getBundlesForUser(user.getId());
		if(bundles != null) {
			payload.setBundles(bundles);
		}
		
		// get the next ids for client-side use
		Map<String, Integer[]> nextIds = getNextIdMap();
		payload.setNextIds(nextIds);

		status.addMsg("User Context retrieved.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		return payload;
	}

	@Override
	public Payload registerUser(UserRegistrationRequest request) {
		Status status = new Status();

		PersistContext persistContext = getPersistContext();
		UserService userService = persistContext.getUserService();

		String name = request.getName();
		String emailAddress = request.getEmailAddress();
		String password = request.getPassword();

		try {
			userService.create(name, emailAddress, password);
		}
		catch(EntityExistsException e) {
			status.addMsg("Email already exists", MsgLevel.ERROR, MsgAttr.EXCEPTION.flag | MsgAttr.FIELD.flag, "userEmail");
		}

		return new Payload(status);
	}

	@Override
	public Payload addBundleUserBinding(String userId, String bundleId) {
		final Status status = new Status();
		Payload payload = new Payload(status);
		
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();
		
		try {
			userDataService.addBundleUserBinding(userId, bundleId);
			status.addMsg("User Bundle binding created.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final EntityExistsException e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
		}
		catch(final RuntimeException e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public Payload addDocUserBinding(String userId, String docId) {
		final Status status = new Status();
		Payload payload = new Payload(status);
		
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();
		
		try {
			userDataService.addDocUserBinding(userId, docId);
			status.addMsg("User Document binding created.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final EntityExistsException e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
		}
		catch(final RuntimeException e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public Payload removeBundleUserBinding(String userId, String bundleId) {
		final Status status = new Status();
		Payload payload = new Payload(status);
		
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();
		
		try {
			userDataService.removeBundleUserBinding(userId, bundleId);
			status.addMsg("User Bundle binding removed.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final EntityNotFoundException e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
		}
		catch(final RuntimeException e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public Payload removeDocUserBinding(String userId, String docId) {
		final Status status = new Status();
		Payload payload = new Payload(status);
		
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();
		
		try {
			userDataService.removeDocUserBinding(userId, docId);
			status.addMsg("User Document binding removed.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final EntityNotFoundException e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
		}
		catch(final RuntimeException e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	public Payload requestPassword(final String emailAddress) {
		final Status status = new Status();
		final Payload p = new Payload(status);
		final Map<String, Object> data = new HashMap<String, Object>();

		if(StringUtil.isEmpty(emailAddress)) {
			status.addMsg("An email address must be specified.", MsgLevel.ERROR, MsgAttr.STATUS.flag);
		}
		else {
			PersistContext context = getPersistContext();
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
	
	private Map<String, Integer[]> getNextIdMap() {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		long[] bundleIdRange = userDataService.generateQuoteBundleIds(100);
		long[] quoteIdRange = userDataService.generateQuoteIds(100);

		Integer[] iBundleIdRange = new Integer[] {
			Integer.valueOf((int) bundleIdRange[0]), Integer.valueOf((int) bundleIdRange[1]),
		};

		Integer[] iQuoteIdRange = new Integer[] {
			Integer.valueOf((int) quoteIdRange[0]), Integer.valueOf((int) quoteIdRange[1]),
		};

		HashMap<String, Integer[]> idMap = new HashMap<String, Integer[]>(2);
		idMap.put(EntityType.QUOTE_BUNDLE.name(), iBundleIdRange);
		idMap.put(EntityType.QUOTE.name(), iQuoteIdRange);
		
		return idMap;
	}

	@Override
	public IdsPayload fetchIdBatch() {
		Status status = new Status();
		IdsPayload payload = new IdsPayload(status);
		payload.setIds(getNextIdMap());
		return payload;
	}

	@Override
	public ModelPayload<QuoteBundle> addBundleForUser(String userId, QuoteBundle bundle) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		Status status = new Status();
		ModelPayload<QuoteBundle> payload = new ModelPayload<QuoteBundle>(status);

		try {
			bundle = userDataService.addBundleForUser(userId, bundle);

			payload.setModel(bundle);
			status.addMsg("Bundle created.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final EntityExistsException e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
		}
		catch(final ConstraintViolationException cve) {
			PersistHelper.handleValidationException(context, cve, payload);
		}
		catch(final RuntimeException e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public ModelPayload<Quote> addQuoteToBundle(String bundleId, Quote quote) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		Status status = new Status();
		ModelPayload<Quote> payload = new ModelPayload<Quote>(status);

		try {
			quote = userDataService.addQuoteToBundle(bundleId, quote);
			payload.setModel(quote);
			status.addMsg("Quote added.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final EntityExistsException e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
		}
		catch(final ConstraintViolationException ise) {
			PersistHelper.handleValidationException(context, ise, payload);
		}
		catch(final RuntimeException e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public Payload deleteBundleForUser(String userId, String bundleId, boolean deleteQuotes) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		Status status = new Status();
		Payload payload = new Payload(status);

		try {
			userDataService.deleteBundleForUser(userId, bundleId, deleteQuotes);
			status.addMsg("Bundle deleted.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final EntityNotFoundException e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
		}
		catch(final RuntimeException e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public Payload removeQuoteFromBundle(String bundleId, String quoteId, boolean deleteQuote) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		Status status = new Status();
		Payload payload = new Payload(status);

		try {
			userDataService.removeQuoteFromBundle(bundleId, quoteId, deleteQuote);
			status.addMsg("Quote removed.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final EntityNotFoundException e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
		}
		catch(final RuntimeException e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public ModelPayload<QuoteBundle> saveBundleForUser(String userId, QuoteBundle qb) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		Status status = new Status();
		ModelPayload<QuoteBundle> payload = new ModelPayload<QuoteBundle>(status);

		try {
			qb = userDataService.saveBundleForUser(userId, qb);
			payload.setModel(qb);
			status.addMsg("Bundle saved.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final EntityExistsException e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
		}
		catch(final ConstraintViolationException ise) {
			PersistHelper.handleValidationException(context, ise, payload);
		}
		catch(final RuntimeException e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public Payload updateBundlePropsForUser(String userId, QuoteBundle bundle) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		Status status = new Status();
		Payload payload = new Payload(status);

		try {
			userDataService.updateBundlePropsForUser(userId, bundle);
			status.addMsg("Quote Bundle properties saved.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final EntityExistsException e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
		}
		catch(final ConstraintViolationException ise) {
			PersistHelper.handleValidationException(context, ise, payload);
		}
		catch(final RuntimeException e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			RpcServlet.exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}
}
