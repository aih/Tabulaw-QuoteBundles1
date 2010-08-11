package com.tabulaw.rest.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.tabulaw.common.model.QuoteBundle;

@XmlRootElement(name = "quoteBundle")
public class QuoteBundleWithDocRefs extends QuoteBundle {
	private static final long serialVersionUID = 1L;
	
	private List<DocRefWithQuotes> docRefs;

	public QuoteBundleWithDocRefs() {
	}
	
	public QuoteBundleWithDocRefs(QuoteBundle bundle) {
		bundle.doClone(this);
	}
	
	@XmlElement(name = "docRef")
	@XmlElementWrapper(name = "docRefs")
	public List<DocRefWithQuotes> getDocRefs() {
		return docRefs;
	}

	public void setDocRefs(List<DocRefWithQuotes> docRefs) {
		this.docRefs = docRefs;
	}

}
