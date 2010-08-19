package com.tabulaw.service.convert;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tabulaw.service.DocUtils;
import com.tabulaw.service.convert.simplehtmlconverter.ElementBuilderFactory;
import com.tabulaw.service.convert.simplehtmlconverter.element.ElementBuilder;
import com.tabulaw.service.convert.simplehtmlconverter.writer.IDocumentContext;

/**
 * @author Andrey Levchenko
 */
abstract class AbstractSimpleHtmlConvertor extends AbstractFileConverter {

	protected abstract IDocumentContext createDocumentContext();

	@Override
	public void convert(InputStream input, OutputStream output) throws Exception {
		final String BODY_TAG_NAME = "body";
		Reader reader = null;
		IDocumentContext documentContext = null;
		
		documentContext = createDocumentContext();
		reader = new InputStreamReader(input, "UTF-8");
		
		documentContext.getDocumentWriter().init(output);
		Element root = loadDocument(reader);
		NodeList bodies = root.getElementsByTagName(BODY_TAG_NAME);
		if(bodies.getLength() > 0) {
			Element body = (Element) bodies.item(0);
			processDomElement(body, documentContext);
		}
		documentContext.getDocumentWriter().close();		
	}

	@Override
	public boolean isFileConvertable(File f) {
		return DocUtils.isHtmlFileByExtension(f.getName());
	}

	@Override
	public String getSourceMimeType() {
		return "text/html";
	}

	private Element loadDocument(Reader r) throws Exception {
		Document doc = null;
		try {
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode root = cleaner.clean(r);
			doc = new DomSerializer(cleaner.getProperties(), true).createDOM(root);
			return doc.getDocumentElement();
		}
		catch(Exception se) {
			throw new Exception(se.getMessage());
		}
	}

	private void processDomElement(Node node, IDocumentContext parentDocumentContext) throws Exception {
		ElementBuilder elementBuilder = ElementBuilderFactory.getElementBuilder(node);
		elementBuilder.process(node, parentDocumentContext);
		for(int i = 0; i < node.getChildNodes().getLength(); i++) {
			Node child = node.getChildNodes().item(i);
			processDomElement(child, parentDocumentContext);
		}
		elementBuilder.afterProcessChilds(node, parentDocumentContext);
	}
}
