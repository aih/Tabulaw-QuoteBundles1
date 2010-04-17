/**
 * The Logic Lab
 * @author jpk
 * @since Mar 26, 2010
 */
package com.tabulaw.server.scrape;

import java.net.URL;
import java.util.List;

import com.tabulaw.common.data.dto.CaseDocSearchResult;
import com.tabulaw.common.data.rpc.DocSearchRequest;
import com.tabulaw.common.data.rpc.DocSearchRequest.DocDataProvider;
import com.tabulaw.common.model.DocRef;

/**
 * API for remote doc search and remote doc screen scraping.
 * @author jpk
 */
public interface IDocHandler {

	/**
	 * @return The doc data type this implementation supports.
	 */
	DocDataProvider getDocDataType();

	/**
	 * Is the url string supported by this doc service?
	 * @param surl
	 * @return
	 */
	boolean isSupportedUrl(String surl);
	
	/**
	 * Creates a doc search {@link URL} from a request specific to the supporting
	 * doc data type.
	 * @param request a search request
	 * @return newly created url
	 * @throws IllegalArgumentException
	 */
	String createSearchUrlString(DocSearchRequest request) throws IllegalArgumentException;

	/**
	 * Parses the given raw html input
	 * @param rawHtml
	 * @return list of native doc search results
	 */
	List<CaseDocSearchResult> parseSearchResults(String rawHtml);

	/**
	 * Parses a single html doc returning the cleaned up and localized version
	 * @param rawHtml doc html as gotten from the source
	 * @return cleaned up native version
	 */
	DocRef parseSingleDocument(String rawHtml);
}
