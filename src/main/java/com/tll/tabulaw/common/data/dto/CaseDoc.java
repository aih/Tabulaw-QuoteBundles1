/**
 * The Logic Lab
 * @author jpk
 * @since Mar 20, 2010
 */
package com.tll.tabulaw.common.data.dto;

import java.util.Date;

/**
 * A data transfer object for a single case type doc.
 * @author jpk
 */
public class CaseDoc extends Doc {

	private String url, citation;

	/**
	 * Constructor
	 * @param title
	 * @param date
	 * @param url
	 * @param citation
	 */
	public CaseDoc(String title, Date date, String url, String citation) {
		super(title, date);
		//this.title = title.replace("<b>", "").replace("</b>", "");
		this.url = url;
		this.citation = citation;
	}

	/**
	 * Constructor
	 */
	public CaseDoc() {
		super();
	}

	public String getUrl() {
		return url;
	}

	public String getCitation() {
		return citation;
	}

	@Override
	public String toString() {
		return "CaseDoc DTO [title=" + getTitle() + "]";
	}

}