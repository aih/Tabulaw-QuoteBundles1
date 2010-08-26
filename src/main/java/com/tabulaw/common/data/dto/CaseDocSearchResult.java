/**
 * The Logic Lab
 * @author jpk
 * @since Mar 31, 2010
 */
package com.tabulaw.common.data.dto;

import java.util.Date;

import com.tabulaw.IMarshalable;
import com.tabulaw.model.IModelKeyProvider;
import com.tabulaw.model.ModelKey;

/**
 * @author jpk
 */
public class CaseDocSearchResult implements IMarshalable, IModelKeyProvider {

	private String title, url, citation, titleHtml, summary;
	private Date date;

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
		this.title = title;
		this.date = date;
		this.url = url;
		this.citation = citation;
		this.titleHtml = titleHtml;
		this.summary = summary;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCitation() {
		return citation;
	}

	public void setCitation(String citation) {
		this.citation = citation;
	}

	public String getTitleHtml() {
		return titleHtml;
	}

	public void setTitleHtml(String titleHtml) {
		this.titleHtml = titleHtml;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public ModelKey getModelKey() {
		return new ModelKey("CaseDocSearchResult", getUrl(), getTitle());
	}
}
