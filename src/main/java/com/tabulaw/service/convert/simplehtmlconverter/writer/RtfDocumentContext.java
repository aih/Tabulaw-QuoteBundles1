package com.tabulaw.service.convert.simplehtmlconverter.writer;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;

public class RtfDocumentContext extends AbstractDocumentContext  {
	private Document document=new Document();
	private Paragraph paragraph=new Paragraph();
	private Phrase phrase = new Phrase();
	
	private RtfDocumentWriter documentWriter = new RtfDocumentWriter();
	
	public RtfDocumentContext(){
		documentWriter.setDocumentContext(this);
	}

	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}
	public Paragraph getParagraph() {
		return paragraph;
	}
	public void setParagraph(Paragraph paragraph) {
		this.paragraph = paragraph;
	}
	public Phrase getPhrase() {
		return phrase;
	}
	public void setPhrase(Phrase phrase) {
		this.phrase = phrase;
	}
	@Override
	public IDocumentWriter getDocumentWriter() {
		return documentWriter;
	}
	
}
