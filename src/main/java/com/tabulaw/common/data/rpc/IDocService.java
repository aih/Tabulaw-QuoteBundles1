/**
 * The Logic Lab
 * @author jpk
 * @since Mar 18, 2010
 */
package com.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tabulaw.common.data.Payload;
import com.tabulaw.common.model.DocRef;

/**
 * Perform document searches based on a provided search token.
 * @author jpk
 */
@RemoteServiceRelativePath(value = "doc")
public interface IDocService extends RemoteService {

	/**
	 * Creates a new doc on the server given a new doc entity with all required
	 * properties set save for the doc hash which is filled in.
	 * @param docRef the new doc entity
	 * @return the created doc wrapped in a doc payload
	 */
	DocPayload createDoc(DocRef docRef);
	
	/**
	 * Updates the contents of an existing (localized) document.
	 * @param docRef the doc containing the contents to update
	 * @return the resultant status wrapped in a payload
	 */
	Payload updateDocContent(DocRef docRef);

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
	DocPayload fetch(String remoteDocUrl);

	/**
	 * Gets the cached docs from the server.
	 * @return the cached doc listing payload
	 */
	DocListingPayload getCachedDocs();
}
