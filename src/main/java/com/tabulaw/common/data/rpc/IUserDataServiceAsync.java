/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tabulaw.common.data.ModelPayload;
import com.tabulaw.common.data.Payload;
import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.Quote;
import com.tabulaw.common.model.QuoteBundle;
import com.tabulaw.common.model.UserState;

/**
 * @author jpk
 */
public interface IUserDataServiceAsync {
	
	void fetchIdBatch(AsyncCallback<IdsPayload> callback);
	
	void saveUserState(UserState userState, AsyncCallback<Void> callback);

	void saveBundleForUser(String userId, QuoteBundle bundle, AsyncCallback<ModelPayload<QuoteBundle>> callback);
	
	void updateBundlePropsForUser(String userId, QuoteBundle bundle, AsyncCallback<Payload> callback);

	void addBundleUserBinding(String userId, String bundleId, AsyncCallback<Payload> callback);
	
	void removeBundleUserBinding(String userId, String bundleId, AsyncCallback<Payload> callback);
	
	void addDocUserBinding(String userId, String docId, AsyncCallback<Payload> callback);
	
	void removeDocUserBinding(String userId, String docId, AsyncCallback<Payload> callback);
	
	void addBundleForUser(String userId, QuoteBundle bundle, AsyncCallback<ModelPayload<QuoteBundle>> callback);

	void deleteBundleForUser(String userId, String bundleId, boolean deleteQuotes, AsyncCallback<Payload> callback);

	void addQuoteToBundle(String userId, String bundleId, Quote quote, AsyncCallback<ModelPayload<Quote>> callback);

	void moveQuote(String userId, String quoteId, String sourceBundleId, String targetBundleId, AsyncCallback<Payload> callback);

	void getDocsForUser(String userId, AsyncCallback<DocListingPayload> callback);
	
	void getAllDocs(AsyncCallback<DocListingPayload> callback);

	void deleteDoc(String docId, AsyncCallback<Payload> callback);
	
	void createDoc(DocRef docRef, AsyncCallback<DocPayload> callback);
	
	void updateDocContent(DocRef docRef, AsyncCallback<Payload> callback);	
}
