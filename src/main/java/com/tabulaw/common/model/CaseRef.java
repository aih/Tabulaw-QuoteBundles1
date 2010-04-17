/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.model;

/**
 * @author jpk
 */
public class CaseRef extends EntityBase {

	private static final long serialVersionUID = 6628199715132440622L;

	private String parties, citation, url;
	private int year;

	/**
	 * Constructor
	 */
	public CaseRef() {
		super();
	}

	/**
	 * Constructor
	 * @param parties
	 * @param citation
	 * @param url
	 * @param year
	 */
	public CaseRef(String parties, String citation, String url, int year) {
		super();
		this.parties = parties;
		this.citation = citation;
		this.url = url;
		this.year = year;
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.CASE;
	}

	public String getParties() {
		return parties;
	}

	public void setParties(String parties) {
		this.parties = parties;
	}

	public String getCitation() {
		return citation;
	}

	public void setCitation(String citation) {
		this.citation = citation;
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
}
