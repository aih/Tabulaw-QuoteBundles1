/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tll.tabulaw.server.rpc;

import javax.validation.ConstraintViolationException;

import com.google.inject.Inject;
import com.tll.common.data.ModelPayload;
import com.tll.common.data.Payload;
import com.tll.common.data.Status;
import com.tll.common.model.Model;
import com.tll.dao.EntityExistsException;
import com.tll.server.rpc.RpcServlet;
import com.tll.server.rpc.entity.AbstractPersistServiceImpl;
import com.tll.server.rpc.entity.PersistContext;
import com.tll.server.rpc.entity.VersionMismatchException;
import com.tll.tabulaw.common.data.rpc.IUserDataService;
import com.tll.tabulaw.common.model.PocEntityType;
import com.tll.tabulaw.model.QuoteBundle;
import com.tll.tabulaw.service.entity.UserDataService;

/**
 * @author jpk
 */
public class UserDataServiceRpc extends RpcServlet implements IUserDataService {

	private static final long serialVersionUID = -8980709251858800516L;
	
	static class UserDataServiceImpl extends AbstractPersistServiceImpl implements IUserDataService {

		public UserDataServiceImpl(PersistContext context) {
			super(context);
		}
		
		UserDataService getService() {
			return context.getEntityServiceFactory().instance(UserDataService.class);
		}

		@Override
		public ModelPayload addBundleForUser(long userId, Model bundle) {
			Status status = new Status();
			ModelPayload payload = new ModelPayload(status);
			
			QuoteBundle qb = context.getMarshaler().marshalModel(bundle, QuoteBundle.class);
			
			qb = getService().addBundleForUser(userId, qb);
			
			try {
				bundle = entityToModel(PocEntityType.QUOTE_BUNDLE, qb);
				payload.setModel(bundle);
			}
			catch(final VersionMismatchException e) {
				RpcServlet.exceptionToStatus(e, payload.getStatus());
			}
			catch(final EntityExistsException e) {
				RpcServlet.exceptionToStatus(e, payload.getStatus());
			}
			catch(final ConstraintViolationException ise) {
				handleValidationException(context, ise, payload);
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
		public ModelPayload addQuoteToBundle(long bundleId, Model mQuote) {
			return null;
		}

		@Override
		public Payload deleteBundleForUser(long userId, Model bundle) {
			return null;
		}

		@Override
		public Payload removeQuoteFromBundle(long bundleId, long quoteId) {
			return null;
		}

		@Override
		public ModelPayload saveBundleForUser(long userId, Model bundle) {
			return null;
		}
		
	}
	
	private final UserDataServiceImpl impl;

	/**
	 * Constructor
	 * @param impl
	 */
	@Inject
	public UserDataServiceRpc(UserDataServiceImpl impl) {
		super();
		this.impl = impl;
	}

	@Override
	public ModelPayload addBundleForUser(long userId, Model bundle) {
		return impl.addBundleForUser(userId, bundle);
	}

	@Override
	public ModelPayload addQuoteToBundle(long bundleId, Model mQuote) {
		return impl.addQuoteToBundle(bundleId, mQuote);
	}

	@Override
	public Payload deleteBundleForUser(long userId, Model bundle) {
		return impl.deleteBundleForUser(userId, bundle);
	}

	@Override
	public Payload removeQuoteFromBundle(long bundleId, long quoteId) {
		return impl.removeQuoteFromBundle(bundleId, quoteId);
	}

	@Override
	public ModelPayload saveBundleForUser(long userId, Model bundle) {
		return impl.saveBundleForUser(userId, bundle);
	}

}
