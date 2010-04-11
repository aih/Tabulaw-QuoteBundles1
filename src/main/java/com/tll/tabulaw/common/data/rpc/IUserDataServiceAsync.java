/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tll.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tll.common.data.ModelPayload;
import com.tll.common.data.Payload;
import com.tll.common.model.Model;


/**
 * @author jpk
 */
public interface IUserDataServiceAsync {

	void saveBundleForUser(long userId, Model bundle, AsyncCallback<ModelPayload> callback);
	
	void addBundleForUser(long userId, Model bundle, AsyncCallback<ModelPayload> callback);
	
	void deleteBundleForUser(long userId, Model bundle, AsyncCallback<Payload> callback);
	
	void addQuoteToBundle(long bundleId, Model mQuote, AsyncCallback<ModelPayload> callback);
	
	void removeQuoteFromBundle(long bundleId, long quoteId, AsyncCallback<Payload> callback);
}
