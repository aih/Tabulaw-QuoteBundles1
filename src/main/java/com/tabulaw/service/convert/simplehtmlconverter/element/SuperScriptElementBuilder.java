package com.tabulaw.service.convert.simplehtmlconverter.element;


import org.w3c.dom.Node;

import com.tabulaw.service.convert.simplehtmlconverter.writer.IDocumentContext;

public class SuperScriptElementBuilder extends DefaultContainerElementBuilder {

	@Override
	protected void modifyPhrase(Node node, IDocumentContext documentContext){
		super.modifyPhrase(node, documentContext);
/*		Chunk ch = new Chunk();
		ch.setTextRise(10);
		documentContext.getPhrase().add(ch);
		// this doesn't work. Chunk should be added to document context and TextElementBuilder should be modified to use chunk if it exists 
*/	}

}
