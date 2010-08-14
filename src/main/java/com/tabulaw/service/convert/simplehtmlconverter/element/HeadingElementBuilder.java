package com.tabulaw.service.convert.simplehtmlconverter.element;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;

import com.tabulaw.service.convert.simplehtmlconverter.writer.IDocumentContext;
import com.tabulaw.service.convert.simplehtmlconverter.writer.info.ParagraphInfo;


public class HeadingElementBuilder extends DefaultContainerElementBuilder {
	private Map<String, Integer> fontSizes= new HashMap<String, Integer>();
	private Map<String, Integer> alignments= new HashMap<String, Integer>();
	public HeadingElementBuilder() {
		fontSizes.put("h1", 18);
		fontSizes.put("h2", 16);
		fontSizes.put("h3", 14);

		alignments.put("h1", ParagraphInfo.ALIGNMENT_CENTER);
		alignments.put("h2", ParagraphInfo.ALIGNMENT_LEFT);
		alignments.put("h3", ParagraphInfo.ALIGNMENT_CENTER);
	}
	@Override
	protected void modifyParagraph(Node node, IDocumentContext documentContext){
		super.modifyParagraph(node, documentContext);
		String tagName=node.getNodeName();
		Integer alignment=ParagraphInfo.ALIGNMENT_CENTER;
		if (alignments.containsKey(tagName)){
			alignment=alignments.get(tagName);
		}
		documentContext.getParagraphInfo().setAlignment(alignment);
	}
	@Override
	protected void modifyPhrase(Node node, IDocumentContext documentContext) {
		super.modifyPhrase(node, documentContext);
		String tagName=node.getNodeName();
		Integer fontSize=12;
		if (fontSizes.containsKey(tagName)){
			fontSize=fontSizes.get(tagName);
		}
		
		documentContext.getRangeInfo().setFontSize(fontSize);
		documentContext.getRangeInfo().setBold(true);
	}
	@Override
	protected boolean isParagraph() {
		return true;
	}
}
