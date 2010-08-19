package com.tabulaw.service.convert.simplehtmlconverter.element;

import org.w3c.dom.Node;

import com.tabulaw.service.convert.simplehtmlconverter.writer.IDocumentContext;


public class BlockQuoteElementBuilder extends DefaultContainerElementBuilder {
	@Override
	protected void modifyParagraph(Node node, IDocumentContext documentContext){
		super.modifyParagraph(node, documentContext);
		documentContext.getParagraphInfo().setIndentationLeft(20.0);
	}
	@Override
	protected boolean isParagraph() {
		return true;
	}
}
