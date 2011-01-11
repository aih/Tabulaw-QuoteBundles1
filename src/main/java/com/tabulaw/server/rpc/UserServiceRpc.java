/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
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

import com.tabulaw.common.data.Status;
import com.tabulaw.common.data.rpc.DocPayload;
import com.tabulaw.common.data.rpc.IUserAdminService;
import com.tabulaw.common.data.rpc.IUserContextService;
import com.tabulaw.common.data.rpc.IUserCredentialsService;
import com.tabulaw.common.data.rpc.IUserDataService;
import com.tabulaw.common.data.rpc.IdsPayload;
import com.tabulaw.common.data.rpc.ModelListPayload;
import com.tabulaw.common.data.rpc.ModelPayload;
import com.tabulaw.common.data.rpc.Payload;
import com.tabulaw.common.data.rpc.UserContextPayload;
import com.tabulaw.common.data.rpc.UserListPayload;
import com.tabulaw.common.data.rpc.UserRegistrationRequest;
import com.tabulaw.common.msg.Msg.MsgAttr;
import com.tabulaw.common.msg.Msg.MsgLevel;
import com.tabulaw.dao.EntityExistsException;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.mail.EmailDispatcher;
import com.tabulaw.mail.IMailContext;
import com.tabulaw.mail.MailManager;
import com.tabulaw.mail.MailRouting;
import com.tabulaw.model.ClauseBundle;
import com.tabulaw.model.ContractDoc;
import com.tabulaw.model.DocContent;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.EntityBase;
import com.tabulaw.model.EntityFactory;
import com.tabulaw.model.EntityType;
import com.tabulaw.model.IUserRef;
import com.tabulaw.model.Quote;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.Reference;
import com.tabulaw.model.User;
import com.tabulaw.model.UserState;
import com.tabulaw.model.User.Role;
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
public class UserServiceRpc extends RpcServlet implements IUserContextService,
		IUserCredentialsService, IUserDataService, IUserAdminService {

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
			for (User user : list) {
				User clnUser = (User) user.clone();
				clnUser.setPassword(null);
				userList.add(clnUser);
			}

			payload.setUsers(userList);
			status.addMsg("Users retrieved.", MsgLevel.INFO,
					MsgAttr.STATUS.flag);
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
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
			status.addMsg("Registration successful.", MsgLevel.INFO,
					MsgAttr.STATUS.flag);

			// send out email
			try {
				sendEmailConfirmation(newUser, webContext);
			} catch (Exception e) {
				// don't penalize for email failure
				status.addMsg(
						"Unable to send email confirmation at this time.",
						MsgLevel.WARN, MsgAttr.STATUS.flag);
			}
		} catch (EntityExistsException e) {
			status.addMsg("Email already exists", MsgLevel.ERROR,
					MsgAttr.EXCEPTION.flag | MsgAttr.FIELD.flag, "userEmail");
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(webContext, e);
			throw e;
		} catch (Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	public static void sendEmailConfirmation(User newUser,
			WebAppContext webContext) throws Exception {
		final EmailDispatcher emailDispatcher = webContext.getEmailDispatcher();
		MailManager mailManager = emailDispatcher.getMailManager();
		final MailRouting mr = mailManager.buildAppSenderMailRouting(newUser
				.getEmailAddress());
		Map<String, Object> data = new HashMap<String, Object>(1);
		data.put("subject", "Tabulaw Registration");
		data.put("emailAddress", newUser.getEmailAddress());
		final IMailContext mailContext = mailManager.buildTextTemplateContext(
				mr, "welcome-user", data);
		try {
			emailDispatcher.queueEmail(mailContext);
		} catch (InterruptedException e) {
			// TODO anything?
		}
	}

	@Override
	public ModelPayload<User> createUser(User user) {
		final Status status = new Status();
		ModelPayload<User> payload = new ModelPayload<User>(status);

		final PersistContext pc = getPersistContext();
		UserService svc = pc.getUserService();

		try {
			String name = user.getName(), email = user.getEmailAddress(), password = user
					.getPassword();
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
		} catch (final ValidationException e) {
			exceptionToStatus(e, payload.getStatus());
		} catch (final EntityExistsException e) {
			exceptionToStatus(e, payload.getStatus());
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
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
		} catch (final EntityNotFoundException e) {
			exceptionToStatus(e, payload.getStatus());
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
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
			status.addMsg("User password set.", MsgLevel.INFO,
					MsgAttr.STATUS.flag);
		} catch (final ChangeUserCredentialsFailedException e) {
			exceptionToStatus(e, status);
			handleException(e);
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
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
		} catch (final EntityNotFoundException e) {
			exceptionToStatus(e, payload.getStatus());
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
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
		} catch (final Exception e) {
			handleException(e);
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			throw new RuntimeException(e);
		}
	}

	@Override
	public UserContextPayload getClientUserContext() {
		final Status status = new Status();
		UserContextPayload payload = new UserContextPayload(status);

		final UserContext userContext = getUserContext();
		if (userContext == null || userContext.getUser() == null) {
			// presume not logged in yet
			status.addMsg("User Context not found.", MsgLevel.INFO,
					MsgAttr.STATUS.flag);
			return payload;
		}

		User user = userContext.getUser();
		payload.setUser(user);

		final PersistContext pc = getPersistContext();

		try {

			// get the retained user state if there is one
			try {
				UserState userState = pc.getUserDataService().getUserState(
						user.getId());
				payload.setUserState(userState);
			} catch (EntityNotFoundException e) {
				// ok
			}

			// get the user's quote bundles
			BundleContainer bc = pc.getUserDataService().getBundlesForUser(
					user.getId());
			payload.setBundles(bc.getBundles());
			payload.setOrphanQuoteContainerId(bc.getOrphanBundleId());

			// get the next ids for client-side use
			Map<String, Integer[]> nextIds = getNextIdMap();
			payload.setNextIds(nextIds);

			status.addMsg("User Context retrieved.", MsgLevel.INFO,
					MsgAttr.STATUS.flag);
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
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
			status.addMsg("User Bundle binding created.", MsgLevel.INFO,
					MsgAttr.STATUS.flag);
		} catch (final EntityExistsException e) {
			exceptionToStatus(e, payload.getStatus());
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
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
			status.addMsg("User Document binding created.", MsgLevel.INFO,
					MsgAttr.STATUS.flag);
		} catch (final EntityExistsException e) {
			exceptionToStatus(e, payload.getStatus());
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
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
			status.addMsg("User Bundle binding removed.", MsgLevel.INFO,
					MsgAttr.STATUS.flag);
		} catch (final EntityNotFoundException e) {
			exceptionToStatus(e, payload.getStatus());
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
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
			status.addMsg("User Document binding removed.", MsgLevel.INFO,
					MsgAttr.STATUS.flag);
		} catch (final EntityNotFoundException e) {
			exceptionToStatus(e, payload.getStatus());
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	public Payload requestPassword(final String emailAddress) {
		final Status status = new Status();
		final Payload p = new Payload(status);

		if (StringUtil.isEmpty(emailAddress)) {
			status.addMsg("An email address must be specified.",
					MsgLevel.ERROR, MsgAttr.STATUS.flag);
		} else {
			WebAppContext wc = getWebAppContext();
			PersistContext pc = getPersistContext();
			UserService userService = pc.getUserService();
			try {
				sendPasswordReminderEmail(emailAddress, userService, wc);
				status.addMsg(
						"Password reminder request processed.  An email should arrive to your inbox within the hour.",
						MsgLevel.INFO, MsgAttr.STATUS.flag);
			} catch (final EntityNotFoundException nfe) {
				exceptionToStatus(nfe, status);
				handleException(wc, nfe);
			} catch (final ChangeUserCredentialsFailedException e) {
				exceptionToStatus(e, status);
				handleException(wc, e);
			} catch (final MailSendException mse) {
				exceptionToStatus(mse, status);
				handleException(wc, mse);
			}
		}

		return p;
	}

	public static void sendPasswordReminderEmail(String emailAddress,
			UserService userService, WebAppContext wc)
			throws ChangeUserCredentialsFailedException {
		final Map<String, Object> data = new HashMap<String, Object>();
		final IUserRef user = userService.getUserRef(emailAddress);
		final String rp = userService.resetPassword(user.getUserRefId());
		data.put("subject", "Tabulaw Password Reminder");
		data.put("username", user.getName());
		data.put("emailAddress", user.getEmailAddress());
		data.put("password", rp);
		final EmailDispatcher emailDispatcher = wc.getEmailDispatcher();
		MailManager mailManager = emailDispatcher.getMailManager();
		final MailRouting mr = mailManager.buildAppSenderMailRouting(user
				.getEmailAddress());
		final IMailContext mailContext = mailManager.buildTextTemplateContext(
				mr, "forgot-password", data);
		try {
			emailDispatcher.queueEmail(mailContext);
		} catch (InterruptedException e) {
			// TODO anything?
		}
	}

	private Map<String, Integer[]> getNextIdMap() {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		long[] bundleIdRange = userDataService.generateQuoteBundleIds(100);
		long[] quoteIdRange = userDataService.generateQuoteIds(100);

		Integer[] iBundleIdRange = new Integer[] {
				Integer.valueOf((int) bundleIdRange[0]),
				Integer.valueOf((int) bundleIdRange[1]), };

		Integer[] iQuoteIdRange = new Integer[] {
				Integer.valueOf((int) quoteIdRange[0]),
				Integer.valueOf((int) quoteIdRange[1]), };

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
	public ModelPayload<QuoteBundle> addBundleForUser(String userId,
			QuoteBundle bundle) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		Status status = new Status();
		ModelPayload<QuoteBundle> payload = new ModelPayload<QuoteBundle>(
				status);

		try {
			bundle = userDataService.addBundleForUser(userId, bundle);

			payload.setModel(bundle);
			status.addMsg("Bundle created.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		} catch (final EntityExistsException e) {
			exceptionToStatus(e, payload.getStatus());
		} catch (final ConstraintViolationException cve) {
			PersistHelper.handleValidationException(context, cve, payload);
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	private int findPage(DocContent document, String position) {
		String[] parts = position.substring(1, position.length() - 1)
				.split(",");
		int[] positionArray = new int[parts.length];
		for (int i = 0; i < parts.length; i++) {
			positionArray[i] = Integer.parseInt(parts[i]);
		}
		if (document == null) {
			return 1;
		}
		if (document.getPagesXPath() == null
				|| document.getPagesXPath().isEmpty()) {
			return document.getFirstPageNumber();
		}
		int pageNumber = document.getFirstPageNumber();
		for (int[] page : document.getPagesXPath()) {
			int comparsionLenght = Math.min(positionArray.length, page.length);
			int element = -1;
			for (int i = 0; i < comparsionLenght; i++) {
				if (positionArray[i] < page[i]) {
					element = i;
					break;
				}
				if (positionArray[i] > page[i]) {
					element = -2;
					break;
				}
			}
			if (element >= 0
					|| (element == -1 && positionArray.length < page.length)) {
				break;
			}
			pageNumber++;
		}
		return pageNumber;
	}

	@Override
	public ModelPayload<Quote> addQuoteToBundle(String userId, String bundleId,
			Quote quote) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		Status status = new Status();
		ModelPayload<Quote> payload = new ModelPayload<Quote>(status);

		DocContent content = userDataService.getDocContent(quote.getDocument()
				.getId());
		String[] elements = quote.getSerializedMark().split("\\|");
		quote.setStartPage(findPage(content, elements[2]));
		quote.setEndPage(findPage(content, elements[4]));

		try {
			quote = userDataService.addQuoteToBundle(userId, bundleId, quote);
			payload.setModel(quote);
			status.addMsg("Quote added.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		} catch (final EntityExistsException e) {
			exceptionToStatus(e, payload.getStatus());
		} catch (final ConstraintViolationException ise) {
			PersistHelper.handleValidationException(context, ise, payload);
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public Payload deleteBundleForUser(String userId, String bundleId,
			boolean deleteQuotes) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		Status status = new Status();
		Payload payload = new Payload(status);

		try {
			userDataService.deleteBundleForUser(userId, bundleId, deleteQuotes);
			status.addMsg("Bundle deleted.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		} catch (final EntityNotFoundException e) {
			exceptionToStatus(e, payload.getStatus());
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
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
		} catch (final EntityNotFoundException e) {
			exceptionToStatus(e, payload.getStatus());
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public Payload moveQuote(String userId, String quoteId,
			String sourceBundleId, String targetBundleId) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		Status status = new Status();
		Payload payload = new Payload(status);

		try {
			userDataService.moveQuote(userId, quoteId, sourceBundleId,
					targetBundleId);
			status.addMsg("Quote moved.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		} catch (final EntityNotFoundException e) {
			exceptionToStatus(e, payload.getStatus());
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public ModelPayload<QuoteBundle> saveBundleForUser(String userId,
			QuoteBundle qb) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		Status status = new Status();
		ModelPayload<QuoteBundle> payload = new ModelPayload<QuoteBundle>(
				status);

		try {
			qb = userDataService.saveBundleForUser(userId, qb);
			payload.setModel(qb);
			status.addMsg("Bundle saved.", MsgLevel.INFO, MsgAttr.STATUS.flag);
		} catch (final EntityExistsException e) {
			exceptionToStatus(e, payload.getStatus());
		} catch (final ConstraintViolationException ise) {
			PersistHelper.handleValidationException(context, ise, payload);
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
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
			status.addMsg("Quote Bundle properties saved.", MsgLevel.INFO,
					MsgAttr.STATUS.flag);
		} catch (final EntityExistsException e) {
			exceptionToStatus(e, payload.getStatus());
		} catch (final ConstraintViolationException ise) {
			PersistHelper.handleValidationException(context, ise, payload);
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public ModelListPayload<DocRef> getAllDocs() {
		Status status = new Status();
		ModelListPayload<DocRef> payload = new ModelListPayload<DocRef>(status);

		UserContext uc = getUserContext();
		User user = uc.getUser();

		if (!user.inRole(Role.ADMINISTRATOR)) {
			status.addMsg("Permission denied.", MsgLevel.ERROR,
					MsgAttr.EXCEPTION.flag);
		} else {
			try {
				List<DocRef> docList = getPersistContext().getUserDataService()
						.getAllDocs();
				payload.setModelList(docList);
			} catch (Exception e) {
				RpcServlet.exceptionToStatus(e, status);
			}
		}

		return payload;
	}

	@Override
	public ModelListPayload<ContractDoc> getAllContractDocs() {
		Status status = new Status();
		ModelListPayload<ContractDoc> payload = new ModelListPayload<ContractDoc>(
				status);

		UserContext uc = getUserContext();
		User user = uc.getUser();

		if (!user.inRole(Role.ADMINISTRATOR)) {
			status.addMsg("Permission denied.", MsgLevel.ERROR,
					MsgAttr.EXCEPTION.flag);
		} else {
			try {
				List<ContractDoc> docList = getPersistContext()
						.getUserDataService().getAllContractDocs();
				payload.setModelList(docList);
			} catch (Exception e) {
				RpcServlet.exceptionToStatus(e, status);
			}
		}

		return payload;
	}

	@Override
	public DocPayload getDoc(String docId) {
		Status status = new Status();
		DocPayload payload = new DocPayload(status);

		PersistContext pc = getPersistContext();
		UserDataService uds = pc.getUserDataService();

		try {
			DocRef docRef = uds.getDoc(docId);
			DocContent docContent = uds.getDocContent(docId);

			payload.setDocRef(docRef);
			payload.setDocContent(docContent);
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public ModelPayload<ContractDoc> getContractDoc(String id) {
		Status status = new Status();
		ModelPayload<ContractDoc> payload = new ModelPayload<ContractDoc>(
				status);

		PersistContext pc = getPersistContext();
		UserDataService uds = pc.getUserDataService();

		try {
			ContractDoc doc = uds.getContractDoc(id);
			payload.setModel(doc);
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public ModelListPayload<DocRef> getDocsForUser(String userId) {
		Status status = new Status();
		ModelListPayload<DocRef> payload = new ModelListPayload<DocRef>(status);

		PersistContext pc = getPersistContext();
		UserDataService uds = pc.getUserDataService();

		try {
			List<DocRef> docList = uds.getDocsForUser(userId);
			payload.setModelList(docList);
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public ModelListPayload<ContractDoc> getContractDocsForUser(String userId) {
		Status status = new Status();
		ModelListPayload<ContractDoc> payload = new ModelListPayload<ContractDoc>(
				status);

		PersistContext pc = getPersistContext();
		UserDataService uds = pc.getUserDataService();

		try {
			List<ContractDoc> docList = uds.getContractDocsForUser(userId);
			payload.setModelList(docList);
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
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
			if (docId == null)
				throw new IllegalArgumentException("Null doc id");

			// user must be an administrator to permanantly delete docs
			User user = getUserContext().getUser();
			if (!user.inRole(Role.ADMINISTRATOR)) {
				throw new Exception("Permission denied.");
			}

			pc.getUserDataService().deleteDoc(docId);

			status.addMsg("Document deleted.", MsgLevel.INFO,
					MsgAttr.STATUS.flag);
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public Payload deleteContractDoc(String id) {
		Status status = new Status();
		Payload payload = new Payload(status);

		final PersistContext pc = getPersistContext();

		try {
			if (id == null)
				throw new IllegalArgumentException("Null contract doc id");

			// user must be an administrator to permanantly delete docs
			User user = getUserContext().getUser();
			if (!user.inRole(Role.ADMINISTRATOR)) {
				throw new Exception("Permission denied.");
			}

			pc.getUserDataService().deleteContractDoc(id);

			status.addMsg("Document deleted.", MsgLevel.INFO,
					MsgAttr.STATUS.flag);
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
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
			if (docId == null)
				throw new IllegalArgumentException("No doc id");
			if (htmlContent == null)
				throw new IllegalArgumentException("No doc content");

			DocContent dc = svc.getDocContent(docId);
			dc.setHtmlContent(htmlContent);
			svc.saveDocContent(dc);

			status.addMsg("Document content updated.", MsgLevel.INFO,
					MsgAttr.STATUS.flag);
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
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
			if (docRef == null || !docRef.isNew())
				throw new IllegalArgumentException("Null or non-new document");

			User user = uc.getUser();
			// int userHash = user.hashCode();
			// int cti = Long.valueOf(System.currentTimeMillis()).hashCode();
			// int hash = Math.abs(userHash ^ cti);

			// stub initial html content if none specified
			if (docContent == null) {
				docContent = "<p><b>Title: </b>" + docRef.getTitle() + "</p>";
				docContent += "<p><b>Creation Date: </b>" + docRef.getDate()
						+ "</p>";
				docContent += "<p><b>Author: </b>" + user.getName() + "</p>";
			}

			UserDataService uds = pc.getUserDataService();

			// save the doc
			DocRef persistedDoc = uds.saveDoc(docRef);
			DocContent dc = EntityFactory.get().buildDocContent(
					persistedDoc.getId(), docContent);
			uds.saveDocContent(dc);

			// save the doc/user binding
			uds.addDocUserBinding(user.getId(), persistedDoc.getId());

			payload.setDocRef(persistedDoc);
			payload.setDocContent(dc);
			status.addMsg("Document created.", MsgLevel.INFO,
					MsgAttr.STATUS.flag);
		} catch (final ConstraintViolationException cve) {
			PersistHelper.handleValidationException(pc, cve, payload);
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public Payload persistContractDoc(ContractDoc doc) {
		Status status = new Status();
		Payload payload = new Payload(status);

		final PersistContext pc = getPersistContext();
		final UserContext uc = getUserContext();

		try {
			if (doc == null || !doc.isNew())
				throw new IllegalArgumentException(
						"Null or non-new contract doc");

			User user = uc.getUser();

			UserDataService uds = pc.getUserDataService();

			// save the doc
			ContractDoc persistedDoc = uds.saveContractDoc(doc);

			// save the doc/user binding
			uds.addContractDocUserBinding(user.getId(), persistedDoc.getId());

			status.addMsg("Contract Document created.", MsgLevel.INFO,
					MsgAttr.STATUS.flag);
		} catch (final ConstraintViolationException cve) {
			PersistHelper.handleValidationException(pc, cve, payload);
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public Payload deleteClauseBundle(String id) {
		Status status = new Status();
		Payload payload = new Payload(status);

		final PersistContext pc = getPersistContext();

		try {
			if (id == null)
				throw new IllegalArgumentException("Null id");

			// user must be an administrator to permanantly clause bundles
			User user = getUserContext().getUser();
			if (!user.inRole(Role.ADMINISTRATOR)) {
				throw new Exception("Permission denied.");
			}

			pc.getUserDataService().deleteClauseBundle(id);

			status.addMsg("Clause Bundle deleted.", MsgLevel.INFO,
					MsgAttr.STATUS.flag);
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public ModelListPayload<ClauseBundle> getAllClauseBundles() {
		Status status = new Status();
		ModelListPayload<ClauseBundle> payload = new ModelListPayload<ClauseBundle>(
				status);

		UserContext uc = getUserContext();
		User user = uc.getUser();

		if (!user.inRole(Role.ADMINISTRATOR)) {
			status.addMsg("Permission denied.", MsgLevel.ERROR,
					MsgAttr.EXCEPTION.flag);
		} else {
			try {
				List<ClauseBundle> list = getPersistContext()
						.getUserDataService().getAllClauseBundles();
				payload.setModelList(list);
			} catch (Exception e) {
				RpcServlet.exceptionToStatus(e, status);
			}
		}

		return payload;
	}

	@Override
	public Payload persistClauseBundle(ClauseBundle cb) {
		Status status = new Status();
		Payload payload = new Payload(status);

		final PersistContext pc = getPersistContext();
		final UserContext uc = getUserContext();

		try {
			User user = uc.getUser();
			if (!user.inRole(Role.ADMINISTRATOR)) {
				status.addMsg("Permission denied.", MsgLevel.ERROR,
						MsgAttr.EXCEPTION.flag);
			} else {
				if (cb == null || !cb.isNew())
					throw new IllegalArgumentException(
							"Null or non-new clause bundle");
				UserDataService uds = pc.getUserDataService();

				// persist
				uds.persistClauseBundle(cb);

				status.addMsg("Clause Bundle persist.", MsgLevel.INFO,
						MsgAttr.STATUS.flag);
			}
		} catch (final ConstraintViolationException cve) {
			PersistHelper.handleValidationException(pc, cve, payload);
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}

	@Override
	public ModelListPayload<EntityBase> addOrphanQuote(String userId,
			String title, Reference reference, String quoteText, String quoteBundleId) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		Status status = new Status();
		ModelListPayload<EntityBase> payload = new ModelListPayload<EntityBase>(
				status);

		List<EntityBase> documentAndBundle = new ArrayList<EntityBase>();
		try {
			Quote quote = userDataService.addOrphanQuote(userId, title, reference,
					quoteText, quoteBundleId);
			documentAndBundle
					.add(userDataService.getQuoteBundle(quoteBundleId));
			documentAndBundle.add(quote.getDocument());
			payload.setModelList(documentAndBundle);
			status.addMsg("User quote added.", MsgLevel.INFO,
					MsgAttr.STATUS.flag);
		} catch (final EntityExistsException e) {
			exceptionToStatus(e, payload.getStatus());
		} catch (final ConstraintViolationException ise) {
			PersistHelper.handleValidationException(context, ise, payload);
		} catch (final RuntimeException e) {
			exceptionToStatus(e, payload.getStatus());
			handleException(e);
			throw e;
		} catch (Exception e) {
			exceptionToStatus(e, payload.getStatus());
		}

		return payload;
	}
}
