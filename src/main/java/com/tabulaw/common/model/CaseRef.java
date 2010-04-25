/**
 * The Logic Lab
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.common.model;

import com.tabulaw.schema.BusinessKeyDef;
import com.tabulaw.schema.BusinessObject;

/**
 * @author jpk
 */
@BusinessObject(businessKeys = @BusinessKeyDef(name = "Url", properties = { "url"
}))
public class CaseRef extends EntityBase implements Comparable<CaseRef> {

	private static final long serialVersionUID = 6628199715132440622L;

	/**
	 * Surrogate primary key.
	 */
	private String id;

	private String parties, citation, url;
	private int year;

	/**
	 * Constructor
	 */
	public CaseRef() {
		super();
	}

	@Override
	public String descriptor() {
		return typeDesc() + " (" + getCitation() + ")";
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		if(id == null) throw new NullPointerException();
		this.id = id;
	}

	@Override
	protected void doClone(IEntity cln) {
		super.doClone(cln);
		CaseRef cr = (CaseRef) cln;
		cr.id = id;
		cr.parties = parties;
		cr.citation = citation;
		cr.url = url;
		cr.year = year;
	}

	@Override
	protected IEntity newInstance() {
		return new CaseRef();
	}

	@Override
	public String getEntityType() {
		return EntityType.CASE.name();
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

	/*
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!super.equals(obj)) return false;
		if(getClass() != obj.getClass()) return false;
		CaseRef other = (CaseRef) obj;
		if(url == null) {
			if(other.url != null) return false;
		}
		else if(!url.equals(other.url)) return false;
		return true;
	}
	*/

	@Override
	public int compareTo(CaseRef o) {
		if(year > o.year) return 1;
		if(o.year > year) return -1;
		if(citation != null && o.citation != null) {
			return citation.compareTo(o.citation);
		}
		return 0;
	}
}
