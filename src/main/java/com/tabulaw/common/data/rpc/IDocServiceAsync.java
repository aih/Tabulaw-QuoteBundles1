/**
 * The Logic Lab
 * @author jpk
 * @since Mar 18, 2010
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tabulaw.common.model.DocRef;

/**
 * @author jpk
 */
public interface IDocServiceAsync {
	
	void createDoc(DocRef docRef, AsyncCallback<DocHashPayload> callback);
	
	void search(DocSearchRequest request, AsyncCallback<DocSearchPayload> callback);
	
	void fetch(String url, AsyncCallback<DocHashPayload> callback);
	
	void getCachedDocs(AsyncCallback<DocListingPayload> callback);
}
