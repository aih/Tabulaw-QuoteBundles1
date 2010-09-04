package com.tabulaw.common.data.dto;

import com.tabulaw.IMarshalable;

/**
 * Encapsulates a single case type document's key attributes.
 * @author jopaki
 */
public class CaseDocData implements IMarshalable {

	private String title, reftoken, parties, docLoc, court, url, htmlContent;
	private int year;

	/**
	 * Constructor
	 */
	public CaseDocData() {
		super();
	}

	/**
	 * Constructor
	 * @param title
	 * @param reftoken
	 * @param parties
	 * @param docLoc
	 * @param court
	 * @param url
	 * @param year
	 * @param htmlContent
	 */
	public CaseDocData(String title, String reftoken, String parties, String docLoc, String court, String url, int year, String htmlContent) {
		super();
		this.title = title;
		this.reftoken = reftoken;
		this.parties = parties;
		this.docLoc = docLoc;
		this.court = court;
		this.url = url;
		this.year = year;
		this.htmlContent = htmlContent;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getReftoken() {
		return reftoken;
	}

	public void setReftoken(String reftoken) {
		this.reftoken = reftoken;
	}

	public String getParties() {
		return parties;
	}

	public void setParties(String parties) {
		this.parties = parties;
	}

	public String getDocLoc() {
		return docLoc;
	}

	public void setDocLoc(String docLoc) {
		this.docLoc = docLoc;
	}

	public String getCourt() {
		return court;
	}

	public void setCourt(String court) {
		this.court = court;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}
}
