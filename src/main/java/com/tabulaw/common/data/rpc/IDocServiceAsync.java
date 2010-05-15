/**
 * The Logic Lab
 * @author jpk
 * @since Mar 18, 2010
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tabulaw.common.data.Payload;
import com.tabulaw.common.model.DocRef;

/**
 * @author jpk
 */
public interface IDocServiceAsync {
	
	void deleteDoc(String docId, AsyncCallback<Payload> callback);
	
	void createDoc(DocRef docRef, AsyncCallback<DocPayload> callback);
	
	void updateDocContent(DocRef docRef, AsyncCallback<Payload> callback);
	
	void search(DocSearchRequest request, AsyncCallback<DocSearchPayload> callback);
	
	void fetch(String url, AsyncCallback<DocPayload> callback);
	
	void getCachedDocs(AsyncCallback<DocListingPayload> callback);
}
