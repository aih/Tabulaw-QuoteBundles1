package com.tabulaw.rest.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "quoteCreation")
public class QuoteCreationData {
	
	private String docRefId;
	private String quoteBundleId;
	private String quoteText;
	
	public String getDocRefId() {
		return docRefId;
	}
	
	public void setDocRefId(String docRefId) {
		this.docRefId = docRefId;
	}
	
	public String getQuoteBundleId() {
		return quoteBundleId;
	}
	
	public void setQuoteBundleId(String quoteBundleId) {
		this.quoteBundleId = quoteBundleId;
	}
	
	public String getQuoteText() {
		return quoteText;
	}
	
	public void setQuoteText(String quoteText) {
		this.quoteText = quoteText;
	}	
}
