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
import com.tabulaw.service.convert.simplehtmlconverter.writer.IDocumentContext;

/**
 * @author Andrey Levchenko
 */
abstract class AbstractSimpleHtmlConvertor extends AbstractFileConverter {


        abstract IDocumentContext createDocumentContext();

	@Override
	public final File convert(File input) throws Exception {
		final String BODY_TAG_NAME = "body";
                File fdoc;
                Reader r = null;
                IDocumentContext documentContext = null;
                try {
                        documentContext = createDocumentContext();
                        fdoc = createSiblingFile(input, getFileExtension());
                        r = new InputStreamReader(new FileInputStream(input), "UTF-8");

                        documentContext.getDocumentWriter().init(fdoc);
                        Element root = loadDocument(r);

                        NodeList bodies = root.getElementsByTagName(BODY_TAG_NAME);
                        if(bodies.getLength() > 0) {
                                Element body = (Element) bodies.item(0);
                                processDomElement(body, documentContext);
                        }

                } finally {
                        if (documentContext !=null && documentContext.getDocumentWriter() != null) {
                            documentContext.getDocumentWriter().close();
                        }
                        r.close();
                }
		return fdoc;
	}

	@Override
	public boolean isFileConvertable(File f) {
		return DocUtils.isHtmlFileByExtension(f.getName());
	}
	
	/**
	 * @return the corresponding file extension token.
	 */
	protected abstract String getFileExtension();

	private Element loadDocument(Reader r) throws Exception {
		Document doc = null;
		try {
			Tidy tidy = new Tidy();
			tidy.setShowWarnings(false);
			tidy.setQuiet(true);
			doc = tidy.parseDOM(r, null);
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
