/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.server.rpc;

import javax.validation.ConstraintViolationException;

import com.tabulaw.common.data.ModelPayload;
import com.tabulaw.common.data.rpc.IUserDataService;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.dao.EntityExistsException;
import com.tabulaw.dao.EntityNotFoundException;
import com.tabulaw.server.PersistContext;
import com.tabulaw.service.entity.UserDataService;
import com.tll.common.data.Payload;
import com.tll.common.data.Status;
import com.tll.common.msg.Msg.MsgAttr;
import com.tll.common.msg.Msg.MsgLevel;
import com.tll.server.rpc.RpcServlet;

/**
 * @author jpk
 */
public class UserDataServiceRpc extends RpcServlet implements IUserDataService {

	private static final long serialVersionUID = -8980709251858800516L;

	private PersistContext getPersistContext() {
		return (PersistContext) getRequestContext().getServletContext().getAttribute(PersistContext.KEY);
	}

	@Override
	public ModelPayload addBundleForUser(String userId, QuoteBundle bundle) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		Status status = new Status();
		ModelPayload payload = new ModelPayload(status);

		try {
			long lUserId = Long.valueOf(userId);
			bundle = userDataService.addBundleForUser(lUserId, bundle);

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
	public ModelPayload addQuoteToBundle(String bundleId, Quote quote) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		Status status = new Status();
		ModelPayload payload = new ModelPayload(status);

		try {
			long lBundleId = Long.valueOf(bundleId);
			quote = userDataService.addQuoteToBundle(lBundleId, quote);
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
	public Payload deleteBundleForUser(String userId, String bundleId) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		Status status = new Status();
		Payload payload = new Payload(status);

		try {
			long lUserId = Long.valueOf(userId);
			long lBundleId = Long.valueOf(bundleId);
			userDataService.deleteBundleForUser(lUserId, lBundleId);
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
			long lBundleId = Long.valueOf(bundleId);
			long lQuoteId = Long.valueOf(quoteId);
			userDataService.removeQuoteFromBundle(lBundleId, lQuoteId, deleteQuote);
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
	public ModelPayload saveBundleForUser(String userId, QuoteBundle qb) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getUserDataService();

		Status status = new Status();
		ModelPayload payload = new ModelPayload(status);

		try {
			long lUserId = Long.valueOf(userId);
			qb = userDataService.saveBundleForUser(lUserId, qb);
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

}
