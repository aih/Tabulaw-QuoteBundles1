package com.tabulaw.service.convert.simplehtmlconverter.element;


import org.w3c.dom.Node;

import com.tabulaw.service.convert.simplehtmlconverter.writer.IDocumentContext;

public class SmallElementBuilder extends DefaultContainerElementBuilder {

	@Override
	protected void modifyPhrase(Node node, IDocumentContext documentContext){
		super.modifyPhrase(node, documentContext);
		documentContext.getRangeInfo().setFontSize(7);
	}

}
