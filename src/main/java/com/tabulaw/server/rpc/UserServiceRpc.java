/**
 * The Logic Lab
 * @author jpk Aug 25, 2007
 */
package com.tabulaw.server.rpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.springframework.mail.MailSendException;

import com.tabulaw.common.data.ModelPayload;
import com.tabulaw.common.data.Payload;
import com.tabulaw.common.data.Status;
import com.tabulaw.common.data.rpc.DocListingPayload;
import com.tabulaw.common.data.rpc.DocPayload;
import com.tabulaw.common.data.rpc.IUserAdminService;
import com.tabulaw.common.data.rpc.IUserContextService;
import com.tabulaw.common.data.rpc.IUserCredentialsService;
import com.tabulaw.common.data.rpc.IUserDataService;
import com.tabulaw.common.data.rpc.IdsPayload;
import com.tabulaw.common.data.rpc.UserContextPayload;
import com.tabulaw.common.data.rpc.UserListPayload;
import com.tabulaw.common.data.rpc.UserRegistrationRequest;
import com.tabulaw.common.model.DocContent;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.EntityFactory;
import com.tabulaw.common.model.EntityType;
import com.tabulaw.common.model.IUserRef;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.common.model.User;
import com.tabulaw.common.model.UserState;
import com.tabulaw.common.model.User.Role;
import com.tabulaw.common.msg.Msg.MsgAttr;
import com.tabulaw.common.msg.Msg.MsgLevel;
import com.tabulaw.dao.EntityExistsException;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.mail.EmailDispatcher;
import com.tabulaw.mail.IMailContext;
import com.tabulaw.mail.MailManager;
import com.tabulaw.mail.MailRouting;
import com.tabulaw.server.PersistContext;
import com.tabulaw.server.UserContext;
import com.tabulaw.server.WebAppContext;
import com.tabulaw.service.entity.ChangeUserCredentialsFailedException;
import com.tabulaw.service.entity.UserDataService;
import com.tabulaw.service.entity.UserService;
import com.tabulaw.service.entity.UserDataService.BundleContainer;
import com.tabulaw.util.StringUtil;

/**
 * @author jpk
 */
public class UserServiceRpc extends RpcServlet implements IUserContextService, IUserCredentialsService, IUserDataService, IUserAdminService {

	private static final long serialVersionUID = 7908647379731614097L;

	@Override
	public UserListPayload getAllUsers() {
		final Status status = new Status();
		UserListPayload payload = new UserListPayload(status);

		final PersistContext pc = getPersistContext();
		UserService svc = pc.getUserService();

		try {
			List<User> list = svc.getAllUsers();
			assert list != null;

			// clear out passwords
			ArrayList<User> userList = new ArrayList<User>(list.size());
			for(User user : list) {
				User clnUser = (User) user.clone();
				clnUser.setPassword(null);
				userList.add(clnUser);
			}

			payload.setUsers(userList);
			status.addMsg("Users retrieved.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			pc.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public Payload registerUser(UserRegistrationRequest request) {
		Status status = new Status();
		Payload payload = new Payload(status);

		PersistContext persistContext = getPersistContext();
		WebAppContext webContext = getWebAppContext();
		UserService userService = persistContext.getUserService();

		String name = request.getName();
		String emailAddress = request.getEmailAddress();
		String password = request.getPassword();

		try {
			User newUser = userService.create(name, emailAddress, password);
			status.addMsg("Registration successful.", MsgLevel.INFO, MsgAttr.STATUS.flag);
			
			// send out email
			try {
				final EmailDispatcher emailDispatcher = webContext.getEmailDispatcher();
				MailManager mailManager = emailDispatcher.getMailManager();
				final MailRouting mr = mailManager.buildAppSenderMailRouting(newUser.getEmailAddress());
				Map<String, Object> data = new HashMap<String, Object>(1);
				data.put("subject", "Tabulaw Registration");
				data.put("emailAddress", newUser.getEmailAddress());
				final IMailContext mailContext = mailManager.buildTextTemplateContext(mr, "welcome-user", data);
				try {
					emailDispatcher.queueEmail(mailContext);
				}
				catch(InterruptedException e) {
					// TODO anything?
				}
			}
			catch(Exception e) {
				// don't penalize for email failure
				status.addMsg("Unable to send email confirmation at this time.", MsgLevel.WARN, MsgAttr.STATUS.flag);
			}
		}
		catch(EntityExistsException e) {
			status.addMsg("Email already exists", MsgLevel.ERROR, MsgAttr.EXCEPTION.flag | MsgAttr.FIELD.flag, "userEmail");
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			persistContext.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public ModelPayload<User> createUser(User user) {
		final Status status = new Status();
		ModelPayload<User> payload = new ModelPayload<User>(status);

		final PersistContext pc = getPersistContext();
		UserService svc = pc.getUserService();

		try {
			String name = user.getName(), email = user.getEmailAddress(), password = user.getPassword();
			User createdUser = svc.create(name, email, password);

			// set remaining user properties
			createdUser.setEnabled(user.isEnabled());
			createdUser.setLocked(user.isLocked());
			createdUser.setRoles(user.getRoles());
			createdUser = svc.updateUser(createdUser);

			// clear out password
			User clnUser = (User) createdUser.clone();
			clnUser.setPassword(null);

			payload.setModel(clnUser);
			status.addMsg("User created.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final ValidationException e) {
			exceptionToStatus(e, payload.getStatus());
		}
		catch(final EntityExistsException e) {
			exceptionToStatus(e, payload.getStatus());
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			pc.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public ModelPayload<User> updateUser(User user) {
		final Status status = new Status();
		ModelPayload<User> payload = new ModelPayload<User>(status);

		final PersistContext pc = getPersistContext();
		UserService svc = pc.getUserService();

		try {
			User updatedUser = svc.updateUser(user);

			// clone updated user and clear out password
			user = (User) updatedUser.clone();
			user.setPassword(null);

			payload.setModel(user);
			status.addMsg("User updated.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final EntityNotFoundException e) {
			exceptionToStatus(e, payload.getStatus());
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			pc.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public Payload setUserPassword(String userId, String password) {
		final Status status = new Status();
		ModelPayload<User> payload = new ModelPayload<User>(status);

		final PersistContext pc = getPersistContext();
		UserService svc = pc.getUserService();

		try {
			svc.setPassword(userId, password);
			status.addMsg("User password set.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final ChangeUserCredentialsFailedException e) {
			exceptionToStatus(e, status);
			pc.getExceptionHandler().handleException(e);
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			pc.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public Payload deleteUser(String userId) {
		final Status status = new Status();
		Payload payload = new Payload(status);

		final PersistContext pc = getPersistContext();
		UserService svc = pc.getUserService();

		try {
			svc.deleteUser(userId);
			status.addMsg("User deleted.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final EntityNotFoundException e) {
			exceptionToStatus(e, payload.getStatus());
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			pc.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public void saveUserState(UserState userState) {
		final PersistContext pc = getPersistContext();
		try {
			UserDataService svc = pc.getUserDataService();
			svc.saveUserState(userState);
		}
		catch(final Exception e) {
			pc.getExceptionHandler().handleException(e);
			if(e instanceof RuntimeException) throw (RuntimeException) e;
			throw new RuntimeException(e);
		}
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

		try {

			// get the retained user state if there is one
			try {
				UserState userState = pc.getUserDataService().getUserState(user.getId());
				payload.setUserState(userState);
			}
			catch(EntityNotFoundException e) {
				// ok
			}

			// get the user's quote bundles
			BundleContainer bc = pc.getUserDataService().getBundlesForUser(user.getId());
			payload.setBundles(bc.getBundles());
			payload.setOrphanQuoteContainerId(bc.getOrphanBundleId());

			// get the next ids for client-side use
			Map<String, Integer[]> nextIds = getNextIdMap();
			payload.setNextIds(nextIds);

			status.addMsg("User Context retrieved.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			pc.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
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
			exceptionToStatus(e, payload.getStatus());
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
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
			exceptionToStatus(e, payload.getStatus());
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
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
			exceptionToStatus(e, payload.getStatus());
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
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
			exceptionToStatus(e, payload.getStatus());
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
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
			WebAppContext wc = getWebAppContext();
			PersistContext pc = getPersistContext();
			UserService userService = pc.getUserService();
			try {
				final IUserRef user = userService.getUserRef(emailAddress);
				final String rp = userService.resetPassword(user.getUserRefId());
				data.put("subject", "Tabulaw Password Reminder");
				data.put("username", user.getName());
				data.put("emailAddress", user.getEmailAddress());
				data.put("password", rp);
				final EmailDispatcher emailDispatcher = wc.getEmailDispatcher();
				MailManager mailManager = emailDispatcher.getMailManager();
				final MailRouting mr = mailManager.buildAppSenderMailRouting(user.getEmailAddress());
				final IMailContext mailContext = mailManager.buildTextTemplateContext(mr, "forgot-password", data);
				try {
					emailDispatcher.queueEmail(mailContext);
				}
				catch(InterruptedException e) {
					// TODO anything?
				}
				status.addMsg("Password reminder request processed.  An email should arrive to your inbox within the hour.", MsgLevel.INFO, MsgAttr.STATUS.flag);
			}
			catch(final EntityNotFoundException nfe) {
				exceptionToStatus(nfe, status);
				pc.getExceptionHandler().handleException(nfe);
			}
			catch(final ChangeUserCredentialsFailedException e) {
				exceptionToStatus(e, status);
				pc.getExceptionHandler().handleException(e);
			}
			catch(final MailSendException mse) {
				exceptionToStatus(mse, status);
				pc.getExceptionHandler().handleException(mse);
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
			exceptionToStatus(e, payload.getStatus());
		}
		catch(final ConstraintViolationException cve) {
			PersistHelper.handleValidationException(context, cve, payload);
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public ModelPayload<Quote> addQuoteToBundle(String userId, String bundleId, Quote quote) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		Status status = new Status();
		ModelPayload<Quote> payload = new ModelPayload<Quote>(status);

		try {
			quote = userDataService.addQuoteToBundle(userId, bundleId, quote);
			payload.setModel(quote);
			status.addMsg("Quote added.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final EntityExistsException e) {
			exceptionToStatus(e, payload.getStatus());
		}
		catch(final ConstraintViolationException ise) {
			PersistHelper.handleValidationException(context, ise, payload);
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
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
			exceptionToStatus(e, payload.getStatus());
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public Payload deleteQuote(String userId, String quoteId) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		Status status = new Status();
		Payload payload = new Payload(status);

		try {
			userDataService.deleteQuote(userId, quoteId);
			status.addMsg("Quote deleted.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final EntityNotFoundException e) {
			exceptionToStatus(e, payload.getStatus());
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public Payload moveQuote(String userId, String quoteId, String sourceBundleId, String targetBundleId) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		Status status = new Status();
		Payload payload = new Payload(status);

		try {
			userDataService.moveQuote(userId, quoteId, sourceBundleId, targetBundleId);
			status.addMsg("Quote moved.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final EntityNotFoundException e) {
			exceptionToStatus(e, payload.getStatus());
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
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
			exceptionToStatus(e, payload.getStatus());
		}
		catch(final ConstraintViolationException ise) {
			PersistHelper.handleValidationException(context, ise, payload);
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
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
			exceptionToStatus(e, payload.getStatus());
		}
		catch(final ConstraintViolationException ise) {
			PersistHelper.handleValidationException(context, ise, payload);
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			context.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public DocListingPayload getAllDocs() {
		Status status = new Status();
		DocListingPayload payload = new DocListingPayload(status);

		UserContext uc = getUserContext();
		User user = uc.getUser();

		if(!user.inRole(Role.ADMINISTRATOR)) {
			status.addMsg("Permission denied.", MsgLevel.ERROR, MsgAttr.EXCEPTION.flag);
		}
		else {
			try {
				List<DocRef> docList = getPersistContext().getUserDataService().getAllDocs();
				payload.setDocList(docList);
			}
			catch(Exception e) {
				RpcServlet.exceptionToStatus(e, status);
			}
		}

		return payload;
	}

	@Override
	public DocListingPayload getDocsForUser(String userId) {
		Status status = new Status();
		DocListingPayload payload = new DocListingPayload(status);

		PersistContext pc = getPersistContext();
		UserDataService uds = pc.getUserDataService();

		try {
			List<DocRef> docList = uds.getDocsForUser(userId);
			payload.setDocList(docList);
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			pc.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public Payload deleteDoc(String docId) {
		Status status = new Status();
		Payload payload = new Payload(status);

		final PersistContext pc = getPersistContext();

		try {
			if(docId == null) throw new IllegalArgumentException("Null doc id");

			// user must be an administrator to permanantly delete docs
			User user = getUserContext().getUser();
			if(!user.inRole(Role.ADMINISTRATOR)) {
				throw new Exception("Permission denied.");
			}

			pc.getUserDataService().deleteDoc(docId);

			status.addMsg("Document deleted.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			pc.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public Payload updateDocContent(String docId, String htmlContent) {
		Status status = new Status();
		Payload payload = new Payload(status);

		final PersistContext pc = getPersistContext();
		final UserDataService svc = pc.getUserDataService();

		try {
			if(docId == null) throw new IllegalArgumentException("No doc id");
			if(htmlContent == null) throw new IllegalArgumentException("No doc content");

			DocContent dc = svc.getDocContent(docId);
			dc.setHtmlContent(htmlContent);
			svc.saveDocContent(dc);

			status.addMsg("Document content updated.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			pc.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public DocPayload createDoc(DocRef docRef, String docContent) {
		Status status = new Status();
		DocPayload payload = new DocPayload(status);

		final PersistContext pc = getPersistContext();
		final UserContext uc = getUserContext();

		try {
			if(docRef == null || !docRef.isNew()) throw new IllegalArgumentException("Null or non-new document");

			User user = uc.getUser();
			// int userHash = user.hashCode();
			// int cti = Long.valueOf(System.currentTimeMillis()).hashCode();
			// int hash = Math.abs(userHash ^ cti);

			// stub initial html content if none specified
			if(docContent == null) {
				docContent = "<p><b>Title: </b>" + docRef.getTitle() + "</p>";
				docContent += "<p><b>Creation Date: </b>" + docRef.getDate() + "</p>";
				docContent += "<p><b>Author: </b>" + user.getName() + "</p>";
			}

			UserDataService uds = pc.getUserDataService();

			// save the doc
			DocRef persistedDoc = uds.saveDoc(docRef);
			DocContent dc = EntityFactory.get().buildDocContent(persistedDoc.getId(), docContent);
			uds.saveDocContent(dc);

			// save the doc/user binding
			uds.addDocUserBinding(uc.getUser().getId(), persistedDoc.getId());

			payload.setDocRef(persistedDoc);
			status.addMsg("Document created.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		}
		catch(final ConstraintViolationException cve) {
			PersistHelper.handleValidationException(pc, cve, payload);
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			pc.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	/*
	@Override
	public Payload exportDoc(String docId, String userId) {
		Status status = new Status();
		Payload payload = new Payload(status);

		final PersistContext pc = getPersistContext();

		DataConverterDelegate fcd =
				(DataConverterDelegate) getServletContext().getAttribute(DataConverterBootstrapper.FILE_CONVERTER_KEY);

		try {
			// load the doc
			DocContent doc = pc.getUserDataService().getDocContent(docId);

			// load the user
			User user = pc.getUserService().loadUser(userId);

			// convert the doc to MS Word
			File f = DocUtils.docContentsToFile(doc);
			File fconverted = fcd.convert(f, "text/html");

			// email the doc
			final MailManager mailManager = pc.getMailManager();
			final MailRouting mr = mailManager.buildAppSenderMailRouting(user.getEmailAddress());
			final IMailContext mailContext = mailManager.buildTextTemplateContext(mr, EMAIL_TEMPLATE_DOC_EXPORT, null);
			FileDataSource fds = new FileDataSource(fconverted);
			mailContext.addAttachment(fconverted.getName(), fds);
			mailManager.sendEmail(mailContext);
			status.addMsg("Document emailed.", MsgLevel.INFO, MsgAttr.STATUS.flag);

			// clean up
			// TODO ensure this doesn't delete the file before emailing it!
			f.delete();
			fconverted.delete();
		}
		catch(final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			pc.getExceptionHandler().handleException(e);
			throw e;
		}
		catch(Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}
	*/
}
