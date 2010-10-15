/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tabulaw.model.ClauseBundle;
import com.tabulaw.model.ContractDoc;
import com.tabulaw.model.DocRef;
import com.tabulaw.model.EntityBase;
import com.tabulaw.model.Quote;
import com.tabulaw.model.QuoteBundle;
import com.tabulaw.model.UserState;

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

	void deleteQuote(String userId, String quoteId, AsyncCallback<Payload> callback);

	void addQuoteToBundle(String userId, String bundleId, Quote quote, AsyncCallback<ModelPayload<Quote>> callback);

	void moveQuote(String userId, String quoteId, String sourceBundleId, String targetBundleId,
			AsyncCallback<Payload> callback);

	void getDocsForUser(String userId, AsyncCallback<ModelListPayload<DocRef>> callback);

	void getContractDocsForUser(String userId, AsyncCallback<ModelListPayload<ContractDoc>> callback);
	
	void getAllDocs(AsyncCallback<ModelListPayload<DocRef>> callback);

	void getAllContractDocs(AsyncCallback<ModelListPayload<ContractDoc>> callback);

	void getDoc(String docId, AsyncCallback<DocPayload> callback);
	
	void getContractDoc(String id, AsyncCallback<ModelPayload<ContractDoc>> callback);
	
	void deleteDoc(String docId, AsyncCallback<Payload> callback);

	void deleteContractDoc(String id, AsyncCallback<Payload> callback);
	
	void createDoc(DocRef docRef, String htmlContent, AsyncCallback<DocPayload> callback);
	
	void persistContractDoc(ContractDoc doc, AsyncCallback<Payload> callback);

	void updateDocContent(String docId, String htmlContent, AsyncCallback<Payload> callback);
	
	void getAllClauseBundles(AsyncCallback<ModelListPayload<ClauseBundle>> callback);
	
	void persistClauseBundle(ClauseBundle cb, AsyncCallback<Payload> callback);
	
	void deleteClauseBundle(String id, AsyncCallback<Payload> callback);

	void addOrphanQuote(String userId, String title, String quoteText, String quoteBundleId, AsyncCallback<ModelListPayload<EntityBase>> callback);
}
