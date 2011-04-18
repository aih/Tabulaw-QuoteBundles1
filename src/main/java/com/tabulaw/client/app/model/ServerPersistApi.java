/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since May 18, 2010
 */
package com.tabulaw.client.app.model;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tabulaw.client.app.Poc;
import com.tabulaw.client.ui.Notifier;
import com.tabulaw.common.data.rpc.ModelPayload;
import com.tabulaw.common.data.rpc.Payload;
import com.tabulaw.model.Quote;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.UserState;

/**
 * @author jpk
 */
public class ServerPersistApi {

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
			Poc.getUserDataService().saveUserState(userState, new AsyncCallback<Void>() {

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

	public void saveBundle(QuoteBundle bundle) {
		if(!doServerPersist) return;
		String userId = ClientModelCache.get().getUser().getId();
		Poc.getUserDataService().saveBundleForUser(userId, bundle, new AsyncCallback<ModelPayload<QuoteBundle>>() {

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
		Poc.getUserDataService().updateBundlePropsForUser(userId, bundle, new AsyncCallback<Payload>() {

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
		addBundle(bundle, null);
	}

	public void addBundle(QuoteBundle bundle, final Command cmd) {
		if(!doServerPersist) return;
		String userId = ClientModelCache.get().getUser().getId();
		Poc.getUserDataService().addBundleForUser(userId, bundle, new AsyncCallback<ModelPayload<QuoteBundle>>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
				if(cmd != null) cmd.execute();
			}

			@Override
			public void onSuccess(ModelPayload<QuoteBundle> result) {
				handlePersistResponse(result);
				if(cmd != null) cmd.execute();
			}
		});
	}

	public void deleteBundle(String bundleId, boolean deleteQuotes) {
		if(!doServerPersist) return;
		String userId = ClientModelCache.get().getUser().getId();
		Poc.getUserDataService().deleteBundleForUser(userId, bundleId, deleteQuotes, new AsyncCallback<Payload>() {

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

	/**
	 * add quote to bundle and notify about this
	 * @param bundleId
	 * @param quote
	 * @param callback
	 */
	public void addQuoteToBundle(String bundleId, Quote quote, final AsyncCallback<Quote> callback) {
		if(!doServerPersist) return;
		String userId = ClientModelCache.get().getUser().getId();
		Poc.getUserDataService().addQuoteToBundle(userId, bundleId, quote, new AsyncCallback<ModelPayload<Quote>>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
				if (callback != null) {
					callback.onFailure(caught);
				}
			}

			@Override
			public void onSuccess(ModelPayload<Quote> result) {
				handlePersistResponse(result);
				if (callback != null) {
					callback.onSuccess(result.getModel());
				}
			}
		});
	}

	public void moveQuote(String quoteId, String sourceBundleId, String targetBundleId) {
		if(!doServerPersist) return;
		String userId = ClientModelCache.get().getUser().getId();
		Poc.getUserDataService().moveQuote(userId, quoteId, sourceBundleId, targetBundleId, new AsyncCallback<Payload>() {

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
	public void attachQuote(String quoteId, String bundleId) {
		if(!doServerPersist) return;
		String userId = ClientModelCache.get().getUser().getId();
		Poc.getUserDataService().attachQuote(userId, quoteId, bundleId, new AsyncCallback<Payload>() {

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

	public void deleteQuote(String bundleId, String quoteId) {
		if(!doServerPersist) return;
		String userId = ClientModelCache.get().getUser().getId();
		Poc.getUserDataService().deleteQuote(userId, bundleId, quoteId, new AsyncCallback<Payload>() {

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
		Poc.getUserDataService().addBundleUserBinding(userId, bundleId, new AsyncCallback<Payload>() {

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
		Poc.getUserDataService().addDocUserBinding(userId, docId, new AsyncCallback<Payload>() {

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
		Poc.getUserDataService().removeBundleUserBinding(userId, bundleId, new AsyncCallback<Payload>() {

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
		Poc.getUserDataService().removeDocUserBinding(userId, docId, new AsyncCallback<Payload>() {

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
	public void shareBundle(QuoteBundle bundle, final Command cmd) {
		if(!doServerPersist) return;
		String userId = ClientModelCache.get().getUser().getId();
		Poc.getUserDataService().shareBundleForUser(userId, bundle, new AsyncCallback<ModelPayload<QuoteBundle>>() {

			@Override
			public void onFailure(Throwable caught) {
				Notifier.get().showFor(caught);
				if(cmd != null) cmd.execute();
			}

			@Override
			public void onSuccess(ModelPayload<QuoteBundle> result) {
				handlePersistResponse(result);
				if(cmd != null) cmd.execute();
			}
		});
	}
	
}
