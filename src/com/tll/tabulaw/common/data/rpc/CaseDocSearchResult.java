/**
 * The Logic Lab
 * @author jpk
 * @since Mar 20, 2010
 */
package com.tll.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class CaseDocSearchResult implements IsSerializable, Suggestion {

	private String title, url, citation, summary;
	private String displayString;

	/**
	 * Constructor
	 * @param title
	 * @param url
	 * @param citation
	 * @param summary
	 */
	public CaseDocSearchResult(String title, String url, String citation, String summary) {
		super();
		this.title = title.replace("<b>", "").replace("</b>", "");
		this.url = url;
		this.citation = citation;
		this.summary = summary;

		this.displayString =
				"<div class=\"entry\"><div class=\"title\">" + title + "</div><div class=\"summary\">" + summary
						+ "</div></div>";
	}

	/**
	 * Constructor
	 */
	public CaseDocSearchResult() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public String getCitation() {
		return citation;
	}

	public String getSummary() {
		return summary;
	}

	@Override
	public String getDisplayString() {
		return displayString;
	}

	@Override
	public String getReplacementString() {
		return title;
	}

	@Override
	public String toString() {
		return "CaseDocSearchResult [title=" + title + "]";
	}

}