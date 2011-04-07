/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.EntityBase;
import com.tabulaw.model.Quote;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.Reference;
import com.tabulaw.model.UserState;

/**
 * @author jpk
 */
public interface IUserDataServiceAsync {

	void saveUserState(UserState userState, AsyncCallback<Void> callback);

	void saveBundleForUser(String userId, QuoteBundle bundle, AsyncCallback<ModelPayload<QuoteBundle>> callback);

	void updateBundlePropsForUser(String userId, QuoteBundle bundle, AsyncCallback<Payload> callback);

	void addBundleUserBinding(String userId, String bundleId, AsyncCallback<Payload> callback);

	void removeBundleUserBinding(String userId, String bundleId, AsyncCallback<Payload> callback);

	void addDocUserBinding(String userId, String docId, AsyncCallback<Payload> callback);

	void removeDocUserBinding(String userId, String docId, AsyncCallback<Payload> callback);

	void addBundleForUser(String userId, QuoteBundle bundle, AsyncCallback<ModelPayload<QuoteBundle>> callback);

	void deleteBundleForUser(String userId, String bundleId, boolean deleteQuotes, AsyncCallback<Payload> callback);

	void deleteQuote(String userId, String bundleId, String quoteId, AsyncCallback<Payload> callback);

	void addQuoteToBundle(String userId, String bundleId, Quote quote, AsyncCallback<ModelPayload<Quote>> callback);

	void moveQuote(String userId, String quoteId, String sourceBundleId, String targetBundleId,
			AsyncCallback<Payload> callback);

	void getDocsForUser(String userId, AsyncCallback<ModelListPayload<DocRef>> callback);

	void getAllDocs(AsyncCallback<ModelListPayload<DocRef>> callback);

	void getDoc(String docId, AsyncCallback<DocPayload> callback);
	
	void deleteDoc(String docId, AsyncCallback<Payload> callback);

	void createDoc(DocRef docRef, String htmlContent, AsyncCallback<DocPayload> callback);
	
	void updateDocContent(String docId, String htmlContent, AsyncCallback<Payload> callback);
	
	void addOrphanQuote(String userId, String title, Reference reference, String quoteText, String quoteBundleId, AsyncCallback<ModelListPayload<EntityBase>> callback);

	void attachQuote(String userId, String quoteId, String bundleId, AsyncCallback<Payload> callback);
}
