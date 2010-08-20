package com.tabulaw.rest.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "quoteCreation")
public class QuoteCreationData {
	
	private String docRefId;
	private String quoteBundleId;
	private String quoteText;
	
	private List<String> quotes;
	
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
	
	@XmlElementWrapper(name = "quotes")
	@XmlElement(name = "quoteText")
	public List<String> getQuotes() {
		return quotes;
	}
	
	public void setQuotes(List<String> quotes) {
		this.quotes = quotes;
	}	
}
