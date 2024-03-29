/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Mar 18, 2010
 */
package com.tabulaw.common.data.rpc;

import java.util.List;

import com.tabulaw.common.data.Status;
import com.tabulaw.common.data.dto.CaseDocSearchResult;

/**
 * Conveys doc search results from server to client.
 * @author jpk
 */
public class DocSearchPayload extends Payload {

	private List<CaseDocSearchResult> results;

	/**
	 * Constructor
	 */
	public DocSearchPayload() {
		super();
	}

	/**
	 * Constructor
	 * @param status
	 */
	public DocSearchPayload(Status status) {
		super(status);
	}

	/**
	 * @return Map of html tokens (key) representing a single search result mapped
	 *         to the corres. doc url.
	 */
	public List<CaseDocSearchResult> getResults() {
		return results;
	}

	public void setResults(List<CaseDocSearchResult> results) {
		this.results = results;
	}
}
