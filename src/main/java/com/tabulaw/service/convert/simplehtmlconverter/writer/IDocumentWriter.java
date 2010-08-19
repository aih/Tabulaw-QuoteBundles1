package com.tabulaw.service.convert.simplehtmlconverter.writer;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

import org.w3c.dom.Node;


public interface IDocumentWriter extends Closeable {
	public IDocumentContext getDocumentContext();
	public void setDocumentContext(IDocumentContext documentContext);
	public void addSoftLineBreak(Node node);
	public void addParagraphToDoc(Node node) throws Exception;
	public void setPharagraphSettings(Node node);
	public void setPhraseSettings(Node node);
	public void addText(String text);
	public void init(OutputStream output) throws Exception;
	public void close() throws IOException;

}
