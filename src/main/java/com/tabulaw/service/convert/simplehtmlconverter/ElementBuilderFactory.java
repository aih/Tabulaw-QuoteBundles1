package com.tabulaw.service.convert.simplehtmlconverter;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.tabulaw.service.convert.simplehtmlconverter.element.DefaultContainerElementBuilder;
import com.tabulaw.service.convert.simplehtmlconverter.element.BlockQuoteElementBuilder;
import com.tabulaw.service.convert.simplehtmlconverter.element.BoldElementBuilder;
import com.tabulaw.service.convert.simplehtmlconverter.element.BrElementBuilder;
import com.tabulaw.service.convert.simplehtmlconverter.element.CenterElementBuilder;
import com.tabulaw.service.convert.simplehtmlconverter.element.ElementBuilder;
import com.tabulaw.service.convert.simplehtmlconverter.element.HeadingElementBuilder;
import com.tabulaw.service.convert.simplehtmlconverter.element.ItalicElementBuilder;
import com.tabulaw.service.convert.simplehtmlconverter.element.ParagraphElementBuilder;
import com.tabulaw.service.convert.simplehtmlconverter.element.SmallElementBuilder;
import com.tabulaw.service.convert.simplehtmlconverter.element.SuperScriptElementBuilder;
import com.tabulaw.service.convert.simplehtmlconverter.element.TextElementBuilder;


public class ElementBuilderFactory {
	private static Map<String, ElementBuilder> elementBuilders = new HashMap<String, ElementBuilder>(); 
	static {
		elementBuilders.put("b",new BoldElementBuilder());
		elementBuilders.put("br",new BrElementBuilder());
		elementBuilders.put("p",new ParagraphElementBuilder());
		elementBuilders.put("center",new CenterElementBuilder());
		elementBuilders.put("h1",new HeadingElementBuilder());
		elementBuilders.put("h2",new HeadingElementBuilder());
		elementBuilders.put("h3",new HeadingElementBuilder());
		elementBuilders.put("i",new ItalicElementBuilder());
		elementBuilders.put("blockquote",new BlockQuoteElementBuilder());
		elementBuilders.put("small",new SmallElementBuilder());
		elementBuilders.put("sup",new SuperScriptElementBuilder());
	}
	public static ElementBuilder getElementBuilder(Node node) {
		ElementBuilder elementBuilder = null;
		if (node.getNodeType()==Element.TEXT_NODE) {
			return new TextElementBuilder();
		}
		String nodeType=node.getNodeName();
		if (elementBuilders.containsKey(nodeType)) {
			elementBuilder=elementBuilders.get(nodeType);
		}else {
			elementBuilder=new DefaultContainerElementBuilder();
		}
		return elementBuilder;
	}

}
