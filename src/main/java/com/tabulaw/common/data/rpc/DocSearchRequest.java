/**
 * The Logic Lab
 * @author jpk
 * @since Mar 18, 2010
 */
package com.tabulaw.common.data.rpc;

import com.tabulaw.IDescriptorProvider;
import com.tabulaw.IMarshalable;

/**
 * Holds the search term(s) of sought documents.
 * @author jpk
 */
public class DocSearchRequest implements IMarshalable, IDescriptorProvider {

	public static enum DocDataProvider {
		GOOGLE_SCHOLAR;
	}

	private DocDataProvider dataProvider;

	private String searchToken;

	private int offset;

	private int numResults;

	private boolean fullTextSearch;

	/**
	 * Constructor
	 */
	public DocSearchRequest() {
		super();
	}

	/**
	 * Constructor
	 * @param dataProvider
	 * @param searchToken
	 * @param offset
	 * @param numResults
	 * @param fullTextSearch 
	 */
	public DocSearchRequest(DocDataProvider dataProvider, String searchToken, int offset, int numResults,
			boolean fullTextSearch) {
		super();
		this.dataProvider = dataProvider;
		this.searchToken = searchToken;
		this.offset = offset;
		this.numResults = numResults;
		this.fullTextSearch = fullTextSearch;
	}

	public String getSearchToken() {
		return searchToken;
	}

	public DocDataProvider getDataProvider() {
		return dataProvider;
	}

	public void setDataProvider(DocDataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	public void setSearchToken(String searchToken) {
		this.searchToken = searchToken;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getNumResults() {
		return numResults;
	}

	public void setNumResults(int numResults) {
		this.numResults = numResults;
	}

	public boolean isFullTextSearch() {
		return fullTextSearch;
	}

	public void setFullTextSearch(boolean fullTextSearch) {
		this.fullTextSearch = fullTextSearch;
	}

	@Override
	public String descriptor() {
		return "Document search request for term(s): " + searchToken;
	}
}
