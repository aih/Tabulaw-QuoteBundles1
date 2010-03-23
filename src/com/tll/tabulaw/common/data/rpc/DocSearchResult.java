/**
 * The Logic Lab
 * @author jpk
 * @since Mar 20, 2010
 */
package com.tll.tabulaw.common.data.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class DocSearchResult implements IsSerializable, Suggestion {
	
	private String docTitleHtml, docUrl, citationText, docSummaryHtml;
	private String displayString, replacementString;

	/**
	 * Constructor
	 * @param docTitleHtml
	 * @param docUrl
	 * @param citationText
	 * @param docSummaryHtml
	 */
	public DocSearchResult(String docTitleHtml, String docUrl, String citationText, String docSummaryHtml) {
		super();
		this.docTitleHtml = docTitleHtml;
		this.docUrl = docUrl;
		this.citationText = citationText;
		this.docSummaryHtml = docSummaryHtml;
		
		this.displayString = "<div class=\"entry\"><div class=\"title\">" + docTitleHtml + "</div><div class=\"summary\">" + docSummaryHtml + "</div></div>";
		this.replacementString = docTitleHtml;
	}

	/**
	 * Constructor
	 */
	public DocSearchResult() {
		super();
	}

	public String getDocTitleHtml() {
		return docTitleHtml;
	}

	public void setDocTitleHtml(String docTitleHtml) {
		this.docTitleHtml = docTitleHtml;
	}

	public String getDocUrl() {
		return docUrl;
	}

	public void setDocUrl(String docUrl) {
		this.docUrl = docUrl;
	}

	public String getCitationText() {
		return citationText;
	}

	public void setCitationText(String citationText) {
		this.citationText = citationText;
	}

	public String getDocSummaryHtml() {
		return docSummaryHtml;
	}

	public void setDocSummaryHtml(String docSummaryHtml) {
		this.docSummaryHtml = docSummaryHtml;
	}

	@Override
	public String getDisplayString() {
		return displayString;
	}

	@Override
	public String getReplacementString() {
		return replacementString;
	}

	@Override
	public String toString() {
		return "DocSearchResult [citationText=" + citationText + ", docSummaryHtml=" + docSummaryHtml + ", docTitleHtml="
				+ docTitleHtml + ", docUrl=" + docUrl + "]";
	}
}