/**
 * The Logic Lab
 * @author jpk
 * @since Mar 18, 2010
 */
package com.tll.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author jpk
 */
public interface IDocSearchServiceAsync {

	/**
	 * @param request
	 * @param callback
	 */
	void search(DocSearchRequest request, AsyncCallback<DocSearchPayload> callback);
}
