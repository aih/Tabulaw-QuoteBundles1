/**
 * The Logic Lab
 * @author jpk
 * @since Mar 31, 2010
 */
package com.tabulaw.common.data.dto;

import java.util.Date;

import com.tabulaw.common.model.IModelKeyProvider;
import com.tabulaw.common.model.ModelKey;

/**
 * @author jpk
 */
public class CaseDocSearchResult extends CaseDoc implements IModelKeyProvider {

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

	@Override
	public ModelKey getModelKey() {
		return new ModelKey("CaseDocSearchResult", getUrl(), getTitle());
	}

	public String getTitleHtml() {
		return titleHtml;
	}

	public String getSummary() {
		return summary;
	}
}
