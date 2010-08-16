package com.tabulaw.service.convert.simplehtmlconverter.writer;

import java.io.File;

import org.w3c.dom.Node;


public interface IDocumentWriter {
	public IDocumentContext getDocumentContext();
	public void setDocumentContext(IDocumentContext documentContext);
	public void addSoftLineBreak(Node node);
	public void addParagraphToDoc(Node node) throws Exception;
	public void setPharagraphSettings(Node node);
	public void setPhraseSettings(Node node);
	public void addText(String text);
	public void init(File outputFile) throws Exception;
	public void close() throws Exception;

}
