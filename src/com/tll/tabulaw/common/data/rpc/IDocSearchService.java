/**
 * The Logic Lab
 * @author jpk
 * @since Mar 18, 2010
 */
package com.tll.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Perform document searches based on a provided search token.
 * @author jpk
 */
@RemoteServiceRelativePath(value = "doc/search")
public interface IDocSearchService extends RemoteService {
	
	/**
	 * Provides search results for the given doc request.
	 * @param request the doc search request
	 * @return the search results payload
	 */
	DocSearchPayload search(DocSearchRequest request);
}
