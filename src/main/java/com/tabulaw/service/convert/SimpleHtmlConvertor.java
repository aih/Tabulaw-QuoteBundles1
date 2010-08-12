package com.tabulaw.service.convert;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import com.tabulaw.service.DocUtils;
import com.tabulaw.service.convert.simplehtmlconverter.ElementBuilderFactory;
import com.tabulaw.service.convert.simplehtmlconverter.element.ElementBuilder;
import com.tabulaw.service.convert.simplehtmlconverter.writer.Docx4jDocumentContext;
import com.tabulaw.service.convert.simplehtmlconverter.writer.IDocumentContext;

public class SimpleHtmlConvertor extends AbstractFileConverter{

	private Element loadDocument(Reader r) throws Exception {

		Document doc = null;
		try {
			Tidy tidy = new Tidy();
			tidy.setShowWarnings(false);
			tidy.setQuiet(true);
			doc = tidy.parseDOM(r, null);
			return doc.getDocumentElement();
		} catch (Exception se) {
			throw new Exception(se.getMessage());
		}
	}

	private void processDomElement(Node node, IDocumentContext documentContext) throws Exception {
		ElementBuilder elementBuilder = ElementBuilderFactory.getElementBuilder(node);
		elementBuilder.process(node, documentContext);
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			Node child = node.getChildNodes().item(i);
			processDomElement(child, documentContext);
		}
		elementBuilder.afterProcessChilds(node, documentContext);
	}

	@Override
	public File convert(File input) throws Exception {
		final String BODY_TAG_NAME = "body";
		File fdoc = createSiblingFile(input, "docx");
		Reader r = new InputStreamReader( new FileInputStream(input), "UTF-8");
		Docx4jDocumentContext documentContext = new Docx4jDocumentContext();
		documentContext.getDocumentWriter().init(fdoc);
		Element root = loadDocument(r);

		NodeList bodies=root.getElementsByTagName(BODY_TAG_NAME);
		if (bodies.getLength()>0) {
			Element body=(Element)bodies.item(0);
			processDomElement(body, documentContext);
		}
		
		documentContext.getDocumentWriter().close();
		r.close();
		return fdoc;
	}

	@Override
	public String getTargetMimeType() {
		return "application/msword";
	}

	@Override
	public boolean isFileConvertable(File f) {
		return DocUtils.isHtmlFileByExtension(f.getName());
	}

}
