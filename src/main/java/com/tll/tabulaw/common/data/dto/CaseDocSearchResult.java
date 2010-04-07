/**
 * The Logic Lab
 * @author jpk
 * @since Mar 31, 2010
 */
package com.tll.tabulaw.common.data.dto;

import java.util.Date;

/**
 * @author jpk
 */
public class CaseDocSearchResult extends CaseDoc {

	private String titleHtml, summary;

	public CaseDocSearchResult() {
		super();
	}

	/**
	 * Constructor
	 * @param title
	 * @param date
	 * @param url
	 * @param citation
	 * @param titleHtml
	 * @param summary
	 */
	public CaseDocSearchResult(String title, Date date, String url, String citation, String titleHtml, String summary) {
		super(title, date, url, citation);
		this.titleHtml = titleHtml;
		this.summary = summary;
	}

	public String getTitleHtml() {
		return titleHtml;
	}

	public String getSummary() {
		return summary;
	}
}
