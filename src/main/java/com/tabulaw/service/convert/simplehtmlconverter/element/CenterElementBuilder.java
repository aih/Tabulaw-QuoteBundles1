package com.tabulaw.service.convert.simplehtmlconverter.element;

import org.w3c.dom.Node;

import com.tabulaw.service.convert.simplehtmlconverter.writer.IDocumentContext;
import com.tabulaw.service.convert.simplehtmlconverter.writer.info.ParagraphInfo;

public class CenterElementBuilder extends DefaultContainerElementBuilder {
	@Override
	protected boolean isParagraph() {
		return true;
	}
	@Override
	protected void modifyParagraph(Node node, IDocumentContext documentContext) {
		super.modifyParagraph(node, documentContext);
		documentContext.getParagraphInfo().setAlignment(ParagraphInfo.ALIGNMENT_CENTER);
	}
}
