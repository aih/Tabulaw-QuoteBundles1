/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tabulaw.common.data.ModelPayload;
import com.tabulaw.common.data.Payload;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.common.model.UserState;

/**
 * @author jpk
 */
public interface IUserDataServiceAsync {
	
	void saveUserState(UserState userState, AsyncCallback<Void> callback);

	void fetchIdBatch(AsyncCallback<IdsPayload> callback);
	
	void saveBundleForUser(String userId, QuoteBundle bundle, AsyncCallback<ModelPayload> callback);

	void addBundleForUser(String userId, QuoteBundle bundle, AsyncCallback<ModelPayload> callback);

	void deleteBundleForUser(String userId, String bundleId, AsyncCallback<Payload> callback);

	void addQuoteToBundle(String bundleId, Quote quote, AsyncCallback<ModelPayload> callback);

	void removeQuoteFromBundle(String bundleId, String quoteId, boolean deleteQuote, AsyncCallback<Payload> callback);
}
