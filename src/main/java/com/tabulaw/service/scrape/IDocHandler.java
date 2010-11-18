/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Mar 26, 2010
 */
package com.tabulaw.service.scrape;

import java.net.URL;
import java.util.List;

import com.tabulaw.common.data.dto.CaseDocData;
import com.tabulaw.common.data.dto.CaseDocSearchResult;
import com.tabulaw.common.data.rpc.DocSearchRequest;

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
	 * Parses raw html for a single doc record into a newly created doc ref
	 * entity.
	 * @param rawHtml doc html as gotten from the source
	 * @return newly created dto holding the parsed doc properties
	 */
	CaseDocData parseSingleDocument(String rawHtml);
}
