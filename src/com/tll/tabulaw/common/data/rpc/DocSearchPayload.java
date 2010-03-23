/**
 * The Logic Lab
 * @author jpk
 * @since Mar 18, 2010
 */
package com.tll.tabulaw.common.data.rpc;

import java.util.List;

import com.tll.common.data.Payload;
import com.tll.common.data.Status;

/**
 * Conveys doc search results from server to client.
 * @author jpk
 */
public class DocSearchPayload extends Payload {

	private List<DocSearchResult> results;

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
	public List<DocSearchResult> getResults() {
		return results;
	}

	public void setResults(List<DocSearchResult> results) {
		this.results = results;
	}
}
