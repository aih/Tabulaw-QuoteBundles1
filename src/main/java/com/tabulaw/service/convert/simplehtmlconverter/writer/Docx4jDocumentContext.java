package com.tabulaw.service.convert.simplehtmlconverter.writer;

import java.io.OutputStream;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.ObjectFactory;

public class Docx4jDocumentContext extends AbstractDocumentContext {
	private WordprocessingMLPackage wordMLPackage;
	private OutputStream outputStream;
	private ObjectFactory factory = new ObjectFactory();
	private org.docx4j.wml.P p = factory.createP();
	private org.docx4j.wml.PPr ppr = factory.createPPr();
	private Docx4jDocumentWriter documentWriter=new Docx4jDocumentWriter();

	public Docx4jDocumentContext() {
		documentWriter.setDocumentContext(this);
	}
	
	public org.docx4j.wml.RPr getRpr() {
		return rpr;
	}
	
	public void setRpr(org.docx4j.wml.RPr rpr) {
		this.rpr = rpr;
	}

	private org.docx4j.wml.RPr rpr = factory.createRPr();

	public org.docx4j.wml.PPr getPpr() {
		return ppr;
	}

	public void setPpr(org.docx4j.wml.PPr ppr) {
		this.ppr = ppr;
	}


	public  MainDocumentPart getMainDocumentPart() {
		return wordMLPackage.getMainDocumentPart();
	}

	public org.docx4j.wml.P getP() {
		return p;
	}

	public void setP(org.docx4j.wml.P p) {
		this.p = p;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public ObjectFactory getFactory() {
		return factory;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public WordprocessingMLPackage getWordMLPackage() {
		return wordMLPackage;
	}

	public void setWordMLPackage(WordprocessingMLPackage wordMLPackage) {
		this.wordMLPackage = wordMLPackage;
	}

	@Override
	public IDocumentWriter getDocumentWriter() {
		return documentWriter;
	}

}
