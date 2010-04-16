/**
 * The Logic Lab
 * @author jpk
 * @since Mar 18, 2010
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Perform document searches based on a provided search token.
 * @author jpk
 */
@RemoteServiceRelativePath(value = "doc")
public interface IDocService extends RemoteService {
	
	/**
	 * Provides search results for the given doc request.
	 * @param request the doc search request
	 * @return the search results payload
	 */
	DocSearchPayload search(DocSearchRequest request);
	
	/**
	 * @param remoteDocUrl http url of the remote doc to fetch
	 * @return
	 */
	DocFetchPayload fetch(String remoteDocUrl);
	
	/**
	 * Gets the cached docs from the server.
	 * @return the cached doc listing payload
	 */
	DocListingPayload getCachedDocs();
}
