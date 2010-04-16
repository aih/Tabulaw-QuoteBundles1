/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.model;

import com.tll.model.EntityBase;
import com.tll.model.IEntity;

/**
 * @author jpk
 */
public class CaseRef extends EntityBase {

	private static final long serialVersionUID = 6628199715132440622L;

	private String parties, citation, url;
	private int year;

	@Override
	public Class<? extends IEntity> entityClass() {
		return CaseRef.class;
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
