package com.tabulaw.rest.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.tabulaw.model.DocContent;
import com.tabulaw.model.DocRef;

@XmlRootElement(name = "docDetails")
public class DocDetails implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private DocRef docRef;	
	private DocContent docContent;
	
	public DocDetails() {
		
	}

	public DocDetails(DocRef docRef, DocContent docContent) {
		this.docRef = docRef;
		this.docContent = docContent;
	}

	public DocRef getDocRef() {
		return docRef;
	}
	
	public void setDocRef(DocRef docRef) {
		this.docRef = docRef;
	}
	
	public DocContent getDocContent() {
		return docContent;
	}

	public void setDocContent(DocContent docContent) {
		this.docContent = docContent;
	}
}
