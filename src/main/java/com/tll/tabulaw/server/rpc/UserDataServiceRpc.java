/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tll.tabulaw.server.rpc;

import javax.validation.ConstraintViolationException;

import com.tll.common.data.ModelPayload;
import com.tll.common.data.Payload;
import com.tll.common.data.Status;
import com.tll.common.model.Model;
import com.tll.common.msg.Msg.MsgAttr;
import com.tll.common.msg.Msg.MsgLevel;
import com.tll.dao.EntityExistsException;
import com.tll.dao.EntityNotFoundException;
import com.tll.server.rpc.RpcServlet;
import com.tll.server.rpc.entity.PersistContext;
import com.tll.server.rpc.entity.PersistHelper;
import com.tll.tabulaw.common.data.rpc.IUserDataService;
import com.tll.tabulaw.common.model.PocEntityType;
import com.tll.tabulaw.model.Quote;
import com.tll.tabulaw.model.QuoteBundle;
import com.tll.tabulaw.service.entity.UserDataService;

/**
 * @author jpk
 */
public class UserDataServiceRpc extends RpcServlet implements IUserDataService {

	private static final long serialVersionUID = -8980709251858800516L;
	
	private PersistContext getPersistContext() {
		return (PersistContext) getRequestContext().getServletContext().getAttribute(PersistContext.KEY);
	}

	@Override
	public ModelPayload addBundleForUser(String userId, Model bundle) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getEntityServiceFactory().instance(UserDataService.class);
		
		Status status = new Status();
		ModelPayload payload = new ModelPayload(status);
		
		try {
			QuoteBundle qb = context.getMarshaler().marshalModel(bundle, QuoteBundle.class);
			
			long lUserId = Long.valueOf(userId);
			qb = userDataService.addBundleForUser(lUserId, qb);
			
			bundle = PersistHelper.marshal(context, PocEntityType.QUOTE_BUNDLE, qb);
			payload.setModel(bundle);
			status.addMsg("Bundle created.", MsgLevel.INFO, MsgAttr.STATUS.flag);
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
	public ModelPayload addQuoteToBundle(String bundleId, Model mQuote) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getEntityServiceFactory().instance(UserDataService.class);
		
		Status status = new Status();
		ModelPayload payload = new ModelPayload(status);
		
		try {
			Quote q = context.getMarshaler().marshalModel(mQuote, Quote.class);
			
			long lBundleId = Long.valueOf(bundleId);
			q = userDataService.addQuoteToBundle(lBundleId, q);
			
			mQuote = PersistHelper.marshal(context, PocEntityType.QUOTE_BUNDLE, q);
			payload.setModel(mQuote);
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
		UserDataService userDataService = context.getEntityServiceFactory().instance(UserDataService.class);
		
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
		UserDataService userDataService = context.getEntityServiceFactory().instance(UserDataService.class);
		
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
	public ModelPayload saveBundleForUser(String userId, Model bundle) {
		PersistContext context = getPersistContext();
		UserDataService userDataService = context.getEntityServiceFactory().instance(UserDataService.class);
		
		Status status = new Status();
		ModelPayload payload = new ModelPayload(status);
		
		try {
			QuoteBundle qb = context.getMarshaler().marshalModel(bundle, QuoteBundle.class);
			
			long lUserId = Long.valueOf(userId);
			qb = userDataService.saveBundleForUser(lUserId, qb);
			
			bundle = PersistHelper.marshal(context, PocEntityType.QUOTE_BUNDLE, qb);
			payload.setModel(bundle);
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
