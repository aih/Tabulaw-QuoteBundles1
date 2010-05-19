/**
 * The Logic Lab
 * @author jpk
 * @since May 18, 2010
 */
package com.tabulaw.client.app.model;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.common.data.ModelPayload;
import com.tabulaw.common.data.Payload;
import com.tabulaw.common.data.rpc.IUserDataService;
import com.tabulaw.common.data.rpc.IUserDataServiceAsync;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.common.model.UserState;

/**
 * @author jpk
 */
public class ServerPersistApi {

	private static final IUserDataServiceAsync userDataService;

	static {
		userDataService = (IUserDataServiceAsync) GWT.create(IUserDataService.class);
	}

	public static IUserDataServiceAsync getUserDataService() {
		return userDataService;
	}

	private static void handlePersistResponse(Payload payload) {
		Notifier.get().showFor(payload, "Persist op successful.");

		// TODO what do we do with the persisted entity in the payload ???
		// we don't want a collision in accessing the sole entity map which
		// as i see it is possible
	}
	
	private static final ServerPersistApi instance = new ServerPersistApi();
	
	public static ServerPersistApi get() {
		return instance;
	}

	private final boolean doServerPersist;

	/**
	 * Constructor
	 */
	private ServerPersistApi() {
		super();
		doServerPersist = true;
	}

	/**
	 * Persists the user state to the server.
	 * @param cmd optional command to execute upon return irregardless of error.
	 */
	public void saveUserState(final Command cmd) {
		UserState userState = ClientModelCache.get().getUserState();
		if(userState != null) {
			userDataService.saveUserState(userState, new AsyncCallback<Void>() {

				@Override
				public void onSuccess(Void result) {
					if(cmd != null) cmd.execute();
				}

				@Override
				public void onFailure(Throwable caught) {
					if(cmd != null) cmd.execute();
				}
			});
		}
	}

	public void unorphanQuote(String quoteId, String bundleId) {
		if(!doServerPersist) return;
		String userId = ClientModelCache.get().getUser().getId();
		userDataService.unorphanQuote(userId, quoteId, bundleId, new AsyncCallback<Payload>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(Payload result) {
				handlePersistResponse(result);
			}
		});
	}

	public void saveBundle(QuoteBundle bundle) {
		if(!doServerPersist) return;
		String userId = ClientModelCache.get().getUser().getId();
		userDataService.saveBundleForUser(userId, bundle, new AsyncCallback<ModelPayload<QuoteBundle>>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(ModelPayload<QuoteBundle> result) {
				handlePersistResponse(result);
			}
		});
	}

	public void updateBundleProps(QuoteBundle bundle) {
		if(!doServerPersist) return;
		String userId = ClientModelCache.get().getUser().getId();
		userDataService.updateBundlePropsForUser(userId, bundle, new AsyncCallback<Payload>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(Payload result) {
				handlePersistResponse(result);
			}
		});
	}

	public void addBundle(QuoteBundle bundle) {
		if(!doServerPersist) return;
		String userId = ClientModelCache.get().getUser().getId();
		userDataService.addBundleForUser(userId, bundle, new AsyncCallback<ModelPayload<QuoteBundle>>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(ModelPayload<QuoteBundle> result) {
				handlePersistResponse(result);
			}
		});
	}

	public void deleteBundle(String bundleId, boolean deleteQuotes) {
		if(!doServerPersist) return;
		String userId = ClientModelCache.get().getUser().getId();
		userDataService.deleteBundleForUser(userId, bundleId, deleteQuotes, new AsyncCallback<Payload>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(Payload result) {
				handlePersistResponse(result);
			}
		});
	}

	public void addQuoteToBundle(String bundleId, Quote quote) {
		if(!doServerPersist) return;
		String userId = ClientModelCache.get().getUser().getId();
		userDataService.addQuoteToBundle(userId, bundleId, quote, new AsyncCallback<ModelPayload<Quote>>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(ModelPayload<Quote> result) {
				handlePersistResponse(result);
			}
		});
	}

	public void removeQuoteFromBundle(String bundleId, String quoteId, boolean deleteQuote) {
		if(!doServerPersist) return;
		String userId = ClientModelCache.get().getUser().getId();
		userDataService.removeQuoteFromBundle(userId, bundleId, quoteId, deleteQuote, new AsyncCallback<Payload>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(Payload result) {
				handlePersistResponse(result);
			}
		});
	}

	public void addBundleUserBinding(String bundleId) {
		if(!doServerPersist) return;
		String userId = ClientModelCache.get().getUser().getId();
		userDataService.addBundleUserBinding(userId, bundleId, new AsyncCallback<Payload>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(Payload result) {
				handlePersistResponse(result);
			}
		});
	}

	public void addDocUserBinding(String docId) {
		if(!doServerPersist) return;
		String userId = ClientModelCache.get().getUser().getId();
		userDataService.addDocUserBinding(userId, docId, new AsyncCallback<Payload>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(Payload result) {
				handlePersistResponse(result);
			}
		});
	}

	public void removeBundleUserBinding(String bundleId) {
		if(!doServerPersist) return;
		String userId = ClientModelCache.get().getUser().getId();
		userDataService.removeBundleUserBinding(userId, bundleId, new AsyncCallback<Payload>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(Payload result) {
				handlePersistResponse(result);
			}
		});
	}

	public void removeDocUserBinding(String docId) {
		if(!doServerPersist) return;
		String userId = ClientModelCache.get().getUser().getId();
		userDataService.removeDocUserBinding(userId, docId, new AsyncCallback<Payload>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
			}

			@Override
			public void onSuccess(Payload result) {
				handlePersistResponse(result);
			}
		});
	}
}
