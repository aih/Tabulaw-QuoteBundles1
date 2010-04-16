/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tll.common.data.ModelPayload;
import com.tll.common.data.Payload;
import com.tll.common.model.Model;


/**
 * @author jpk
 */
public interface IUserDataServiceAsync {

	void saveBundleForUser(String userId, Model bundle, AsyncCallback<ModelPayload> callback);
	
	void addBundleForUser(String userId, Model bundle, AsyncCallback<ModelPayload> callback);
	
	void deleteBundleForUser(String userId, String bundleId, AsyncCallback<Payload> callback);
	
	void addQuoteToBundle(String bundleId, Model mQuote, AsyncCallback<ModelPayload> callback);
	
	void removeQuoteFromBundle(String bundleId, String quoteId, boolean deleteQuote, AsyncCallback<Payload> callback);
}
