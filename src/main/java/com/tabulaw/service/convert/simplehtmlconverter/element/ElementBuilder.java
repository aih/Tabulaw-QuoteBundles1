package com.tabulaw.service.convert.simplehtmlconverter.element;

import org.w3c.dom.Node;

import com.tabulaw.service.convert.simplehtmlconverter.writer.IDocumentContext;

public interface ElementBuilder {
	public abstract void  process(Node node, IDocumentContext context) throws Exception ;
	public void afterProcessChilds(Node node, IDocumentContext documentContext);
}
