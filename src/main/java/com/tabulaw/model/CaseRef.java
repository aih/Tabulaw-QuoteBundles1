/**
 * Copyright (C) Tabulaw, Inc. 2009-2010 All Rights Reserved
 * @author jpk
 * @since Apr 10, 2010
 */
package com.tabulaw.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.tabulaw.model.bk.BusinessKeyDef;
import com.tabulaw.model.bk.BusinessObject;
import com.tabulaw.util.StringUtil;

/**
 * Ref to a remote case.
 * <p>
 * <pre>
 * reftoken  {original full citation text}
 * url:      {the url pointing to the original doc of which this entity is based
 * parties:  "New York Times Co. v. Sullivan"
 * docLoc:   "376 U.S. 254"
 * court:    "Supreme Court" or "5th Circuit"
 * year:     1975
 * </pre>
 * <p>
 * <b>Supreme court</b> case ref full citation format:
 * 
 * <pre>
 * 	New York Times Co. v. Sullivan, 376 U.S. 254 (1964).
 * </pre>
 * <p>
 * <b>Other</b> case ref full citation format:
 * 
 * <pre>
 * 	Curtis Publishing Company v. Butts, 351 F. 2d 702 (5th Cir. 1965)
 * </pre>
 * @author jpk
 */
@BusinessObject(businessKeys = @BusinessKeyDef(name = "Url", properties = { "url"
}))
@XmlRootElement(name = "caseRef")
public class CaseRef extends Reference implements Comparable<CaseRef> {

	private static final long serialVersionUID = 6628199715132440622L;

	private String reftoken, parties, docLoc, court;
	
	private int year;
	
	private int firstPageNumber;
	
	private int lastPageNumber;

	/**
	 * Constructor
	 */
	public CaseRef() {
		super();
	}

	/**
	 * Is this a ref to a supreme court case?
	 * @return true/false
	 */
	public boolean isSupremeCourt() {
		return court != null && (court.indexOf("Supreme Court") >= 0);
	}

	@Override
	public String format(ReferenceFormat format, Quote quote) {
		switch (format) {
			case HTML: 
					return formatFull(true, quote);					
			case TITLE:
					return parties == null ? "" : parties;					
			case SUB_TITLE:
					return formatSubTitle(quote);
			case SHORT_SUB_TITLE:
				return formatShortSubTitle();					
					
			default:
				return formatFull(false, quote);					
		}
	}

	public String formatSubTitle(Quote quote) {
		StringBuilder subtitleBuilder = new StringBuilder();
		subtitleBuilder.append(getDocLoc());
		subtitleBuilder.append(", ");
			
		if (quote != null && quote.getStartPage() > 0) {
			subtitleBuilder.append(quote.getStartPage());
			if (quote.getStartPage() != quote.getEndPage()) {
				subtitleBuilder.append("-");
				subtitleBuilder.append(quote.getEndPage());
			}
		}
		subtitleBuilder.append(" (");
		if (! isSupremeCourt() && getCourt() != null && ! getCourt().isEmpty()) {
			subtitleBuilder.append(getCourt());
			subtitleBuilder.append(" ");
		}
		subtitleBuilder.append(getYear());
		subtitleBuilder.append(").");
		return subtitleBuilder.toString();
	}
	
	public String formatShortSubTitle() {
		StringBuilder shortSubtitleBuilder = new StringBuilder(); 
		if (isSupremeCourt()) {
			shortSubtitleBuilder
					.append("US (")
					.append(getYear())
					.append(")");
		} else {
			shortSubtitleBuilder.append("(");			
			if (StringUtil.isEmpty(getCourt())) {
				shortSubtitleBuilder.append(getCourt());
				shortSubtitleBuilder.append(" ");
			}
			shortSubtitleBuilder.append(getYear());
			shortSubtitleBuilder.append(")");
		}
		return shortSubtitleBuilder.toString();
	}
	
	public String formatFull(boolean html, Quote quote) {
		StringBuilder caseRefBuilder = new StringBuilder(512);
		
		if (! StringUtil.isEmpty(getParties())) {
			if (html) {
				caseRefBuilder.append("<i>");
			}
			caseRefBuilder.append(getParties());
			caseRefBuilder.append(", ");
			if (html) {
				caseRefBuilder.append("</i>");
			}
		}
		caseRefBuilder.append(getDocLoc());
		if (quote != null && quote.getStartPage() > 0) {
			caseRefBuilder.append(", ");
			caseRefBuilder.append(quote.getStartPage());
			if (quote.getStartPage() != quote.getEndPage()) {
				caseRefBuilder.append("-");
				caseRefBuilder.append(quote.getEndPage());
			}
		}
		caseRefBuilder.append(" (");
		if (! isSupremeCourt() && ! StringUtil.isEmpty(getCourt())) {
			caseRefBuilder.append(getCourt());
			caseRefBuilder.append(" ");
		}
		caseRefBuilder.append(getYear());
		caseRefBuilder.append(").");
		return caseRefBuilder.toString();
	}
	
	@Override
	public String serializeToString() {
		StringBuilder sb = new StringBuilder(512); 
		sb.append("|parties::");
		sb.append(getParties());
		sb.append("|reftoken::");
		sb.append(getReftoken());
		sb.append("|docLoc::");
		sb.append(getDocLoc());
		sb.append("|court::");
		sb.append(getCourt());
		sb.append("|url::");
		sb.append(getUrl());
		sb.append("|year::");
		sb.append(getYear());
		sb.append("|firstPageNumber::");
		sb.append(getFirstPageNumber());
		sb.append("|lastPageNumber::");
		sb.append(getLastPageNumber());		
		return sb.toString();
	}
	
	@Override
	public void deserializeFromString(String data) {
		String[] sarr1 = data.split("\\|");
		
		for(String sub : sarr1) {
			String[] sarr2 = sub.split("::");
			String name = sarr2[0];
			String value = (sarr2.length == 2) ? sarr2[1] : "";
			if("parties".equals(name)) {
				parties = value;
			}
			else if("reftoken".equals(name)) {
				reftoken = value;
			}
			else if("docLoc".equals(name)) {
				docLoc = value;
			}
			else if("court".equals(name)) {
				court = value;
			}
			else if("url".equals(name)) {
				url = value;
			}
			else if("year".equals(name)) {
				year = Integer.parseInt(value);
			} 
			else if ("firstPageNumber".equals(name)) {
				firstPageNumber = Integer.parseInt(value);
			}
			else if ("lastPageNumber".equals(name)) {
				lastPageNumber = Integer.parseInt(value);
			}
		}
	}

	@Override
	protected void doClone(IEntity cln) {
		super.doClone(cln);
		CaseRef cr = (CaseRef) cln;
		cr.parties = parties;
		cr.reftoken = reftoken;
		cr.docLoc = docLoc;
		cr.court = court;
		cr.year = year;
		cr.firstPageNumber = firstPageNumber;
		cr.lastPageNumber = lastPageNumber;
	}

	@Override
	protected IEntity newInstance() {
		return new CaseRef();
	}

	@Override
	public String getEntityType() {
		return EntityType.CASE.name();
	}

	/**
	 * @return the original citation token.
	 */
	public String getReftoken() {
		return reftoken;
	}

	public void setReftoken(String reftoken) {
		this.reftoken = reftoken;
	}

	/**
	 * @return the parties (e.g.:
	 *         "Board of Supervisors of James City Cty. v. Rowe")
	 */
	public String getParties() {
		return parties;
	}

	public void setParties(String parties) {
		this.parties = parties;
	}

	/**
	 * I.e.: "216 SE 2d 199"
	 * @return the citation's doc location ref token
	 */
	public String getDocLoc() {
		return docLoc;
	}

	public void setDocLoc(String docLoc) {
		this.docLoc = docLoc;
	}

	/**
	 * @return the 4-digit numeric case year (e.g.: 1975)
	 */
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @return the court (e.g.: "Supreme Court")
	 */
	public String getCourt() {
		return court;
	}

	public void setCourt(String court) {
		this.court = court;
	}
	
	public int getFirstPageNumber() {
		return firstPageNumber;
	}

	public void setFirstPageNumber(int firstPageNumber) {
		this.firstPageNumber = firstPageNumber;
	}

	public int getLastPageNumber() {
		return lastPageNumber;
	}

	public void setLastPageNumber(int lastPageNuber) {
		this.lastPageNumber = lastPageNuber;
	}

	@Override
	public int compareTo(CaseRef o) {
		if(year > o.year) return 1;
		if(o.year > year) return -1;
		if(parties != null && o.parties != null) {
			return parties.compareTo(o.parties);
		}
		return 0;
	}
}
