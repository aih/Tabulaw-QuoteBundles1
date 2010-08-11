package com.tabulaw.rest.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.tabulaw.common.model.DocRef;
import com.tabulaw.common.model.Quote;

@XmlRootElement(name = "docRef")
public class DocRefWithQuotes extends DocRef {
	
	private static final long serialVersionUID = 1L;
	
	private List<Quote> quotes;
	
	public DocRefWithQuotes() {
		
	}
	
	public DocRefWithQuotes(DocRef docRef) {
		docRef.doClone(this);
	}

	@XmlElement(name = "quote")
	@XmlElementWrapper(name = "quotes")
	public List<Quote> getQuotes() {
		return quotes;
	}

	public void setQuotes(List<Quote> quotes) {
		this.quotes = quotes;
	}
}
